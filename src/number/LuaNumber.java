package number;

public class LuaNumber {

    public static boolean isInteger(double f) {
        return f == (long) f;
    }

    public static Long parseInteger(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double parseFloat(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
