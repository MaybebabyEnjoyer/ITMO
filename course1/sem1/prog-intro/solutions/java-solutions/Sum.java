public class Sum {
    public static void main(String[] args) {
        int sum = 0;
        StringBuilder number = new StringBuilder("");
        for (int i = 0; i < args.length; i++) {
            number.setLength(0);
            for (int j = 0; j < args[i].length(); j++) {
                if (!Character.isWhitespace(args[i].charAt(j))) {
                    number.append(args[i].charAt(j));
                } else if (number.length() > 0) {
                    sum += Integer.parseInt(number.toString());
                    number.setLength(0);
                }
            }
            if (number.length() > 0) {
                sum += Integer.parseInt(number.toString());
            }
        }
        System.out.println(sum);
    }
}