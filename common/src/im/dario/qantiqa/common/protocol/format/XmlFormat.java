package im.dario.qantiqa.common.protocol.format;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message.Builder;

public class XmlFormat extends DefaultHandler {

    /**
     * Thrown by {@link XmlFormat#unescapeBytes} and
     * {@link XmlFormat#unescapeText} when an invalid escape sequence is seen.
     */
    static class InvalidEscapeSequence extends IOException {

        private static final long serialVersionUID = 1L;

        public InvalidEscapeSequence(String description) {
            super(description);
        }
    }

    /**
     * An inner class for writing text to the output stream.
     */
    static private final class XmlGenerator {

        Appendable output;

        public XmlGenerator(Appendable output) {
            this.output = output;
        }

        /**
         * Print text to the output stream.
         */
        public void print(CharSequence text) throws IOException {
            int size = text.length();
            int pos = 0;

            write(text.subSequence(pos, size), size - pos);
        }

        private void write(CharSequence data, int size) throws IOException {
            if (size == 0) {
                return;
            }
            output.append(data);
        }
    }

    private static Pattern DOUBLE_INFINITY = Pattern.compile("-?inf(inity)?",
            Pattern.CASE_INSENSITIVE);
    private static Pattern FLOAT_INFINITY = Pattern.compile("-?inf(inity)?f?",
            Pattern.CASE_INSENSITIVE);
    private static Pattern FLOAT_NAN = Pattern.compile("nanf?",
            Pattern.CASE_INSENSITIVE);

    private static Descriptor type;
    private static Builder builder;
    private static ExtensionRegistry extensionRegistry;

    /**
     * Interpret a character as a digit (in any base up to 36) and return the
     * numeric value. This is like {@code Character.digit()} but we don't accept
     * non-ASCII digits.
     */
    private static int digitValue(char c) {
        if (('0' <= c) && (c <= '9')) {
            return c - '0';
        } else if (('a' <= c) && (c <= 'z')) {
            return c - 'a' + 10;
        } else {
            return c - 'A' + 10;
        }
    }

    /**
     * Escapes bytes in the format used in protocol buffer text format, which is
     * the same as the format used for C string literals. All bytes that are not
     * printable 7-bit ASCII characters are escaped, as well as backslash,
     * single-quote, and double-quote characters. Characters for which no
     * defined short-hand escape sequence is defined will be escaped using
     * 3-digit octal sequences.
     */
    static String escapeBytes(ByteString input) {
        StringBuilder builder = new StringBuilder(input.size());
        for (int i = 0; i < input.size(); i++) {
            byte b = input.byteAt(i);
            switch (b) {
            // Java does not recognize \a or \v, apparently.
            case 0x07:
                builder.append("\\a");
                break;
            case '\b':
                builder.append("\\b");
                break;
            case '\f':
                builder.append("\\f");
                break;
            case '\n':
                builder.append("\\n");
                break;
            case '\r':
                builder.append("\\r");
                break;
            case '\t':
                builder.append("\\t");
                break;
            case 0x0b:
                builder.append("\\v");
                break;
            case '\\':
                builder.append("\\\\");
                break;
            case '\'':
                builder.append("\\\'");
                break;
            case '"':
                builder.append("\\\"");
                break;
            default:
                if (b >= 0x20) {
                    builder.append((char) b);
                } else {
                    builder.append('\\');
                    builder.append((char) ('0' + ((b >>> 6) & 3)));
                    builder.append((char) ('0' + ((b >>> 3) & 7)));
                    builder.append((char) ('0' + (b & 7)));
                }
                break;
            }
        }

        return builder.toString();
    }

    /**
     * Like {@link #escapeBytes(com.google.protobuf.ByteString)}, but escapes a
     * text string. Non-ASCII characters are first encoded as UTF-8, then each
     * byte is escaped individually as a 3-digit octal escape. Yes, it's weird.
     */
    static String escapeText(String input) {
        return escapeBytes(ByteString.copyFromUtf8(input));
    }

    /**
     * Is this a hex digit?
     */
    private static boolean isHex(char c) {
        return (('0' <= c) && (c <= '9')) || (('a' <= c) && (c <= 'f'))
                || (('A' <= c) && (c <= 'F'));
    }

    /**
     * Is this an octal digit?
     */
    private static boolean isOctal(char c) {
        return ('0' <= c) && (c <= '7');
    }

    public static void merge(InputStream input, Builder builder) {
        merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
    }

    public static void merge(InputStream input,
            ExtensionRegistry extensionRegistry, Builder builder) {

        XmlFormat.builder = builder;
        XmlFormat.type = builder.getDescriptorForType();
        XmlFormat.extensionRegistry = extensionRegistry;

        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();

            sp.parse(input, new XmlFormat());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse a 32-bit signed integer from the text. Unlike the Java standard
     * {@code Integer.parseInt()}, this function recognizes the prefixes "0x"
     * and "0" to signify hexidecimal and octal numbers, respectively.
     */
    static int parseInt32(String text) throws NumberFormatException {
        return (int) parseInteger(text, true, false);
    }

    /**
     * Parse a 64-bit signed integer from the text. Unlike the Java standard
     * {@code Integer.parseInt()}, this function recognizes the prefixes "0x"
     * and "0" to signify hexidecimal and octal numbers, respectively.
     */
    static long parseInt64(String text) throws NumberFormatException {
        return parseInteger(text, true, true);
    }

    private static long parseInteger(String text, boolean isSigned,
            boolean isLong) throws NumberFormatException {
        int pos = 0;

        boolean negative = false;
        if (text.startsWith("-", pos)) {
            if (!isSigned) {
                throw new NumberFormatException("Number must be positive: "
                        + text);
            }
            ++pos;
            negative = true;
        }

        int radix = 10;
        if (text.startsWith("0x", pos)) {
            pos += 2;
            radix = 16;
        } else if (text.startsWith("0", pos)) {
            radix = 8;
        }

        String numberText = text.substring(pos);

        long result = 0;
        if (numberText.length() < 16) {
            // Can safely assume no overflow.
            result = Long.parseLong(numberText, radix);
            if (negative) {
                result = -result;
            }

            // Check bounds.
            // No need to check for 64-bit numbers since they'd have to be 16
            // chars
            // or longer to overflow.
            if (!isLong) {
                if (isSigned) {
                    if ((result > Integer.MAX_VALUE)
                            || (result < Integer.MIN_VALUE)) {
                        throw new NumberFormatException(
                                "Number out of range for 32-bit signed integer: "
                                        + text);
                    }
                } else {
                    if ((result >= (1L << 32)) || (result < 0)) {
                        throw new NumberFormatException(
                                "Number out of range for 32-bit unsigned integer: "
                                        + text);
                    }
                }
            }
        } else {
            BigInteger bigValue = new BigInteger(numberText, radix);
            if (negative) {
                bigValue = bigValue.negate();
            }

            // Check bounds.
            if (!isLong) {
                if (isSigned) {
                    if (bigValue.bitLength() > 31) {
                        throw new NumberFormatException(
                                "Number out of range for 32-bit signed integer: "
                                        + text);
                    }
                } else {
                    if (bigValue.bitLength() > 32) {
                        throw new NumberFormatException(
                                "Number out of range for 32-bit unsigned integer: "
                                        + text);
                    }
                }
            } else {
                if (isSigned) {
                    if (bigValue.bitLength() > 63) {
                        throw new NumberFormatException(
                                "Number out of range for 64-bit signed integer: "
                                        + text);
                    }
                } else {
                    if (bigValue.bitLength() > 64) {
                        throw new NumberFormatException(
                                "Number out of range for 64-bit unsigned integer: "
                                        + text);
                    }
                }
            }

            result = bigValue.longValue();
        }

        return result;
    }

    /**
     * Parse a 32-bit unsigned integer from the text. Unlike the Java standard
     * {@code Integer.parseInt()}, this function recognizes the prefixes "0x"
     * and "0" to signify hexidecimal and octal numbers, respectively. The
     * result is coerced to a (signed) {@code int} when returned since Java has
     * no unsigned integer type.
     */
    static int parseUInt32(String text) throws NumberFormatException {
        return (int) parseInteger(text, false, false);
    }

    /**
     * Parse a 64-bit unsigned integer from the text. Unlike the Java standard
     * {@code Integer.parseInt()}, this function recognizes the prefixes "0x"
     * and "0" to signify hexidecimal and octal numbers, respectively. The
     * result is coerced to a (signed) {@code long} when returned since Java has
     * no unsigned long type.
     */
    static long parseUInt64(String text) throws NumberFormatException {
        return parseInteger(text, false, true);
    }

    public static void print(Message message, Appendable output)
            throws IOException {
        XmlGenerator generator = new XmlGenerator(output);
        final String messageName = message.getDescriptorForType().getName();
        generator.print("<");
        generator.print(messageName);
        generator.print(">");
        print(message, generator);
        generator.print("</");
        generator.print(messageName);
        generator.print(">");
    }

    private static void print(Message message, XmlFormat.XmlGenerator generator)
            throws IOException {
        for (Map.Entry<FieldDescriptor, Object> field : message.getAllFields()
                .entrySet()) {
            printField(field.getKey(), field.getValue(), generator);
        }
        printUnknownFields(message.getUnknownFields(), generator);
    }

    public static void printField(FieldDescriptor field, Object value,
            XmlGenerator generator) throws IOException {

        if (field.isRepeated()) {
            // Repeated field. Print each element.
            for (Object element : (List<?>) value) {
                printSingleField(field, element, generator);
            }
        } else {
            printSingleField(field, value, generator);
        }
    }

    private static void printFieldValue(FieldDescriptor field, Object value,
            XmlGenerator generator) throws IOException {
        switch (field.getType()) {
        case INT32:
        case INT64:
        case SINT32:
        case SINT64:
        case SFIXED32:
        case SFIXED64:
        case FLOAT:
        case DOUBLE:
        case BOOL:
            // Good old toString() does what we want for these types.
            generator.print(value.toString());
            break;

        case UINT32:
        case FIXED32:
            generator.print(unsignedToString((Integer) value));
            break;

        case UINT64:
        case FIXED64:
            generator.print(unsignedToString((Long) value));
            break;

        case STRING:
            generator.print(escapeText((String) value));
            break;

        case BYTES: {
            generator.print(escapeBytes((ByteString) value));
            break;
        }

        case ENUM: {
            generator.print(((EnumValueDescriptor) value).getName());
            break;
        }

        case MESSAGE:
        case GROUP:
            print((Message) value, generator);
            break;
        }
    }

    private static void printSingleField(FieldDescriptor field, Object value,
            XmlGenerator generator) throws IOException {
        if (field.isExtension()) {
            generator.print("<extension type=\"");
            // We special-case MessageSet elements for compatibility with
            // proto1.
            if (field.getContainingType().getOptions()
                    .getMessageSetWireFormat()
                    && (field.getType() == FieldDescriptor.Type.MESSAGE)
                    && (field.isOptional())
                    // object equality
                    && (field.getExtensionScope() == field.getMessageType())) {
                generator.print(field.getMessageType().getFullName());
            } else {
                generator.print(field.getFullName());
            }
            generator.print("\">");
        } else {
            generator.print("<");
            if (field.getType() == FieldDescriptor.Type.GROUP) {
                // Groups must be serialized with their original capitalization.
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            generator.print(">");
        }

        printFieldValue(field, value, generator);

        if (!field.isExtension()) {
            generator.print("</");
            if (field.getType() == FieldDescriptor.Type.GROUP) {
                // Groups must be serialized with their original capitalization.
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            generator.print(">");
        } else {
            generator.print("</extension>");
        }

    }

    public static String printToString(Message message) {
        try {
            StringBuilder text = new StringBuilder();
            print(message, text);
            return text.toString();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Writing to a StringBuilder threw an IOException (should never happen).",
                    e);
        }
    }

    private static void printUnknownField(CharSequence fieldKey,
            CharSequence fieldValue, XmlGenerator generator) throws IOException {
        generator.print("<unknown-field index=\"");
        generator.print(fieldKey);
        generator.print("\">");
        generator.print(fieldValue);
        generator.print("</unknown-field>");
    }

    private static void printUnknownFields(UnknownFieldSet unknownFields,
            XmlGenerator generator) throws IOException {
        for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields
                .asMap().entrySet()) {
            UnknownFieldSet.Field field = entry.getValue();

            final String key = entry.getKey().toString();
            for (long value : field.getVarintList()) {
                printUnknownField(key, unsignedToString(value), generator);
            }
            for (int value : field.getFixed32List()) {
                printUnknownField(key, String.format((Locale) null, "0x%08x",
                        value), generator);
            }
            for (long value : field.getFixed64List()) {
                printUnknownField(key, String.format((Locale) null, "0x%016x",
                        value), generator);
            }
            for (ByteString value : field.getLengthDelimitedList()) {
                printUnknownField(key, escapeBytes(value), generator);
            }
            for (UnknownFieldSet value : field.getGroupList()) {
                generator.print("<unknown-field index=\"");
                generator.print(key);
                generator.print("\">");
                printUnknownFields(value, generator);
                generator.print("</unknown-field>");
            }
        }
    }

    static ByteString unescapeBytes(CharSequence input)
            throws InvalidEscapeSequence {
        byte[] result = new byte[input.length()];
        int pos = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\\') {
                if (i + 1 < input.length()) {
                    ++i;
                    c = input.charAt(i);
                    if (isOctal(c)) {
                        // Octal escape.
                        int code = digitValue(c);
                        if ((i + 1 < input.length())
                                && isOctal(input.charAt(i + 1))) {
                            ++i;
                            code = code * 8 + digitValue(input.charAt(i));
                        }
                        if ((i + 1 < input.length())
                                && isOctal(input.charAt(i + 1))) {
                            ++i;
                            code = code * 8 + digitValue(input.charAt(i));
                        }
                        result[pos++] = (byte) code;
                    } else {
                        switch (c) {
                        case 'a':
                            result[pos++] = 0x07;
                            break;
                        case 'b':
                            result[pos++] = '\b';
                            break;
                        case 'f':
                            result[pos++] = '\f';
                            break;
                        case 'n':
                            result[pos++] = '\n';
                            break;
                        case 'r':
                            result[pos++] = '\r';
                            break;
                        case 't':
                            result[pos++] = '\t';
                            break;
                        case 'v':
                            result[pos++] = 0x0b;
                            break;
                        case '\\':
                            result[pos++] = '\\';
                            break;
                        case '\'':
                            result[pos++] = '\'';
                            break;
                        case '"':
                            result[pos++] = '\"';
                            break;

                        case 'x':
                            // hex escape
                            int code = 0;
                            if ((i + 1 < input.length())
                                    && isHex(input.charAt(i + 1))) {
                                ++i;
                                code = digitValue(input.charAt(i));
                            } else {
                                throw new InvalidEscapeSequence(
                                        "Invalid escape sequence: '\\x' with no digits");
                            }
                            if ((i + 1 < input.length())
                                    && isHex(input.charAt(i + 1))) {
                                ++i;
                                code = code * 16 + digitValue(input.charAt(i));
                            }
                            result[pos++] = (byte) code;
                            break;

                        default:
                            throw new InvalidEscapeSequence(
                                    "Invalid escape sequence: '\\" + c + "'");
                        }
                    }
                } else {
                    throw new InvalidEscapeSequence(
                            "Invalid escape sequence: '\\' at end of string.");
                }
            } else {
                result[pos++] = (byte) c;
            }
        }

        return ByteString.copyFrom(result, 0, pos);
    }

    /**
     * Un-escape a text string as escaped using {@link #escapeText(String)}.
     * Two-digit hex escapes (starting with "\x") are also recognized.
     */
    static String unescapeText(String input) throws InvalidEscapeSequence {
        return unescapeBytes(input).toStringUtf8();
    }

    /**
     * Convert an unsigned 32-bit integer to a string.
     */
    private static String unsignedToString(int value) {
        if (value >= 0) {
            return Integer.toString(value);
        } else {
            return Long.toString((value) & 0x00000000FFFFFFFFL);
        }
    }

    /**
     * Convert an unsigned 64-bit integer to a string.
     */
    private static String unsignedToString(long value) {
        if (value >= 0) {
            return Long.toString(value);
        } else {
            // Pull off the most-significant bit so that BigInteger doesn't
            // think
            // the number is negative, then set it again using setBit().
            return BigInteger.valueOf(value & 0x7FFFFFFFFFFFFFFFL).setBit(63)
                    .toString();
        }
    }

    // Context
    private FieldDescriptor field;

    private ExtensionRegistry.ExtensionInfo extension;

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        String data = new String(ch, start, length);

        Object value = handleValue(data);

        if (field.isRepeated()) {
            builder.addRepeatedField(field, value);
        } else {
            builder.setField(field, value);
        }
    }

    private Object handlePrimitive(String data) {
        Object value = null;
        switch (field.getType()) {
        case INT32:
        case SINT32:
        case SFIXED32:
            value = parseInt32(data);
            break;

        case INT64:
        case SINT64:
        case SFIXED64:
            value = parseInt64(data);
            break;

        case UINT32:
        case FIXED32:
            value = parseUInt32(data);
            break;

        case UINT64:
        case FIXED64:
            value = parseUInt64(data);
            break;

        case FLOAT:
            value = parseFloat(data);
            break;

        case DOUBLE:
            value = parseDouble(data);
            break;

        case BOOL:
            value = parseBoolean(data);
            break;

        case STRING:
            value = parseString(data);
            break;

        case BYTES:
            value = parseByteString(data);
            break;

        case ENUM: {
            Descriptors.EnumDescriptor enumType = field.getEnumType();

            if (lookingAtInteger(data)) {
                int number = parseInt32(data);
                value = enumType.findValueByNumber(number);
                if (value == null) {
                    throw new IllegalStateException("Enum type \""
                            + enumType.getFullName()
                            + "\" has no value with number " + number + ".");
                }
            } else {
                String id = data;
                value = enumType.findValueByName(id);
                if (value == null) {
                    throw new IllegalStateException("Enum type \""
                            + enumType.getFullName()
                            + "\" has no value named \"" + id + "\".");
                }
            }

            break;
        }

        case MESSAGE:
        case GROUP:
            throw new RuntimeException("Can't get here.");
        }
        return value;
    }

    private Object handleValue(String data) {
        Object value = null;
        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            throw new RuntimeException("Not implemented");
        } else {
            value = handlePrimitive(data);
        }

        return value;
    }

    /**
     * Returns {@code true} if the next token is an integer, but does not
     * consume it.
     */
    public boolean lookingAtInteger(String data) {
        if (data.length() == 0) {
            return false;
        }

        char c = data.charAt(0);
        return (('0' <= c) && (c <= '9')) || (c == '-') || (c == '+');
    }

    private Object parseBoolean(String data) {
        return Boolean.parseBoolean(data);
    }

    private ByteString parseByteString(String data) {
        try {
            return unescapeBytes(data);
        } catch (InvalidEscapeSequence e) {
            e.printStackTrace();

            return null;
        }
    }

    private double parseDouble(String data) {
        // We need to parse infinity and nan separately because
        // Double.parseDouble() does not accept "inf", "infinity", or "nan".
        if (DOUBLE_INFINITY.matcher(data).matches()) {
            boolean negative = data.startsWith("-");

            return negative ? Double.NEGATIVE_INFINITY
                    : Double.POSITIVE_INFINITY;
        }

        if (data.equalsIgnoreCase("nan")) {
            return Double.NaN;
        }

        return Double.parseDouble(data);
    }

    private float parseFloat(String data) {
        if (FLOAT_INFINITY.matcher(data).matches()) {
            boolean negative = data.startsWith("-");

            return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
        }

        if (FLOAT_NAN.matcher(data).matches()) {
            return Float.NaN;
        }

        return Float.parseFloat(data);
    }

    private String parseString(String data) {
        return parseByteString(data).toStringUtf8();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (qName.equals(type.getName())) {
            return;
        }

        if (qName.startsWith("extension.")) {
            extension = extensionRegistry.findExtensionByName(qName);

            if (extension == null) {
                throw new IllegalStateException("Extension \"" + qName
                        + "\" not found in the ExtensionRegistry.");
            } else if (extension.descriptor.getContainingType() != type) {
                throw new IllegalStateException("Extension \"" + qName
                        + "\" does not extend message type \""
                        + type.getFullName() + "\".");
            }

            field = extension.descriptor;
        } else {
            field = type.findFieldByName(qName);

            // Group names are expected to be capitalized as they appear in the
            // .proto file, which actually matches their type names, not their
            // field
            // names.
            if (field == null) {
                // Explicitly specify US locale so that this code does not break
                // when
                // executing in Turkey.
                String lowerName = qName.toLowerCase(Locale.US);
                field = type.findFieldByName(lowerName);
                // If the case-insensitive match worked but the field is NOT a
                // group,
                if ((field != null)
                        && (field.getType() != FieldDescriptor.Type.GROUP)) {
                    field = null;
                }
            }

            // Again, special-case group names as described above.
            if ((field != null)
                    && (field.getType() == FieldDescriptor.Type.GROUP)
                    && !field.getMessageType().getName().equals(qName)) {
                field = null;
            }

            if (field == null) {
                throw new IllegalStateException("Message type \""
                        + type.getFullName() + "\" has no field named \""
                        + qName + "\".");
            }
        }
    }

}
