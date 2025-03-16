import java.util.*;

public class Calculator {
    private static final List<String> calculationHistory = new ArrayList<>();
    private static final Scanner inputScanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Calculator!");
        while (true) {
            System.out.print("Enter an expression or 'history' to see past results: ");
            String userInput = inputScanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Thanks for using the Calculator!");
                break;
            } else if (userInput.equalsIgnoreCase("history")) {
                showHistory();
                continue;
            }

            try {
                double result = evaluateExpression(userInput);
                System.out.println("Result: " + result);
                calculationHistory.add(userInput + " = " + result);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showHistory() {
        if (calculationHistory.isEmpty()) {
            System.out.println("No previous calculations.");
        } else {
            System.out.println("Calculation History:");
            for (String record : calculationHistory) {
                System.out.println(record);
            }
        }
    }

    private static double evaluateExpression(String expression) throws Exception {
        expression = expression.replaceAll("\\s+", "");
        return processParentheses(expression);
    }

    private static double processParentheses(String expression) throws Exception {
        while (expression.contains("(")) {
            int openBracket = expression.lastIndexOf('(');
            int closeBracket = expression.indexOf(')', openBracket);
            if (closeBracket == -1) {
                throw new Exception("Unmatched parentheses");
            }
            String insideBrackets = expression.substring(openBracket + 1, closeBracket);
            double bracketResult;
            int functionStart = openBracket - 1;
            while (functionStart >= 0 && Character.isLetter(expression.charAt(functionStart))) {
                functionStart--;
            }
            functionStart++;
            String functionName = expression.substring(functionStart, openBracket);
            if (isFunction(functionName)) {
                bracketResult = applyFunction(functionName, insideBrackets);
                expression = expression.substring(0, functionStart) + bracketResult + expression.substring(closeBracket + 1);
            } else {
                bracketResult = processParentheses(insideBrackets);
                expression = expression.substring(0, openBracket) + bracketResult + expression.substring(closeBracket + 1);
            }
        }
        return calculate(expression);
    }

    private static boolean isFunction(String name) {
        return name.equals("abs") || name.equals("sqrt") || name.equals("round") || name.equals("power");
    }

    private static double applyFunction(String function, String argument) throws Exception {
        if (function.equals("power")) {
            String[] arguments = argument.split(",");
            if (arguments.length != 2) {
                throw new Exception("Power function needs two arguments");
            }
            double base = evaluateExpression(arguments[0]);
            double exponent = evaluateExpression(arguments[1]);
            return Math.pow(base, exponent);
        } else if (function.equals("sqrt")) {
            double value = evaluateExpression(argument);
            return Math.sqrt(value);
        } else if (function.equals("abs")) {
            double value = evaluateExpression(argument);
            return Math.abs(value);
        } else if (function.equals("round")) {
            double value = evaluateExpression(argument);
            return Math.round(value);
        } else {
            throw new Exception("Unknown function: " + function);
        }
    }

    private static double calculate(String expression) throws Exception {
        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char symbol = expression.charAt(i);
            if ((symbol >= '0' && symbol <= '9') || symbol == '.' || 
                (symbol == '-' && (i == 0 || "+-*/%".indexOf(expression.charAt(i - 1)) != -1))) {
                currentNumber.append(symbol);
            } else {
                numbers.add(Double.parseDouble(currentNumber.toString()));
                currentNumber = new StringBuilder();
                operators.add(symbol);
            }
        }
        if (currentNumber.length() > 0) {
            numbers.add(Double.parseDouble(currentNumber.toString()));
        }

        for (int i = 0; i < operators.size();) {
            char operator = operators.get(i);
            if (operator == '*' || operator == '/' || operator == '%') {
                double first = numbers.get(i);
                double second = numbers.get(i + 1);
                double result;
                if (operator == '*') {
                    result = first * second;
                } else if (operator == '/') {
                    if (second == 0) throw new ArithmeticException("Division by zero");
                    result = first / second;
                } else {
                    result = first % second;
                }
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
            } else {
                i++;
            }
        }

        double finalResult = numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            char operator = operators.get(i);
            double nextNumber = numbers.get(i + 1);
            if (operator == '+') {
                finalResult += nextNumber;
            } else if (operator == '-') {
                finalResult -= nextNumber;
            }
        }
        return finalResult;
    }
}