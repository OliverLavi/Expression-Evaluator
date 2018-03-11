package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	
	    	//To store each token separately to manipulate them
	    	ArrayList<String> tokens = new ArrayList<String>();
	    	
	    	//Creates tokens
	    	StringTokenizer st = new StringTokenizer(expr, delims, true);
	    	while(st.hasMoreTokens()) {
	    		tokens.add(st.nextToken());
	    	}
	    	
	    	//Deletes any digit tokens
	    	for(int i = 0; i<tokens.size(); i++) {
	    		if(tokens.get(i).matches("\\d+")) {
	    			tokens.remove(i);
	    			i=i-1;
	    		}
	    	}
	    	
	    	//Debug Delete
//	    	for(String tok: tokens) {
//	    		System.out.println(tok);
//	    	}
//	    	System.out.println("__");
	    	
	    	//Deletes any delim tokens
	    	for(int i = 0; i < tokens.size(); i++) {
	    		if(tokens.get(i).matches("\\*")
	    				|| tokens.get(i).matches("\\+")
	    				|| tokens.get(i).matches("-")
	    				|| tokens.get(i).matches("/")
	    				|| tokens.get(i).matches("\\(")
	    				|| tokens.get(i).matches("\\)")
	    				|| tokens.get(i).matches("]")
	    				|| tokens.get(i).matches("\t")
	    				|| tokens.get(i).matches("\\s")) {
	    			tokens.remove(i);
	    			i=i-1;
	    		}
	    	}
	    	
	    	//Debug delete
//	    for(String tok: tokens) {
//	    		System.out.println(tok);
//	    	}
//	    System.out.println("__");
	    
	    //Remove duplicates
	    for(int i = 0; i < tokens.size()-1; i++) {
	    		//For arrays
	    		if(tokens.get(i+1).matches("\\[")) {
	    			for(int j = i+2; j < tokens.size()-1; j++) {
	    				if(tokens.get(j).matches(tokens.get(i)) && tokens.get(j+1).matches("\\[")) {
	    					tokens.remove(j);
	    					tokens.remove(j);
	    					j = i+1;
	    				}
	    			}
	    		//For variables
	    		}else if(!tokens.get(i).matches("\\[")){
	    			for(int j = i+1; j < tokens.size(); j++) {
	    				if(tokens.get(j).matches(tokens.get(i)) && (!tokens.get(j+1).matches("\\[") || tokens.get(j+1) == null)) {
	    					tokens.remove(j);
	    					j = i;
	    				}
	    			}
	    		}
	    }
	    //Remove last item if duplicate variable
	    if(tokens.size() > 1 && !tokens.get(tokens.size()-1).matches("\\[")) {
		    for(int i = 0; i < tokens.size()-1; i++) {
		    		if(tokens.get(i).matches(tokens.get(tokens.size()-1))) {
		    			tokens.remove(tokens.get(tokens.size()-1));
		    		}
		    }
	    }
	    	
	    //Add variables and arrays to lists
	    if(tokens.size()==1) {
	    		vars.add(new Variable(tokens.get(0)));
	    }else if(tokens.size() > 0 || tokens.size() != 1){
		    for(int i = 0; i < tokens.size()-1; i++) {
		    		if(tokens.get(i+1).matches("\\[")) {
		    			Array arr = new Array(tokens.get(i));
		    			arrays.add(arr);
		    			//tokens.remove(i+1);
		    		}else if(!tokens.get(i+1).matches("\\[") && !tokens.get(i).matches("\\[")) {
		    			Variable var = new Variable(tokens.get(i));
		    			vars.add(var);	
		    			
		    		}
		    }
		    if(tokens.size() > 1 && !tokens.get(tokens.size()-1).matches("\\[")) {
		    		vars.add(new Variable(tokens.get(tokens.size()-1)));
		    }
	    }
	    
	    //Debug delete
//	    System.out.println("__");
//	    for(Variable varr: vars) {
//	    		System.out.println(varr.toString());
//	    }
//	    System.out.println("__");
//	    for(Array ar: arrays) {
//	    		System.out.println(ar.toString());
//	    }
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	float res = 0;
    	Stack<Integer> par = new Stack();
    	Stack<String> operators = new Stack();
    	Stack<Float> values = new Stack();
    	expr = expr.trim();
    	
    	//To store each token separately to manipulate them
    	ArrayList<String> tokens = new ArrayList<String>();
    	
    	//Creates tokens
    	StringTokenizer st = new StringTokenizer(expr, delims, true);
    	while(st.hasMoreTokens()) {
    		tokens.add(st.nextToken());
    	}
    	
    	//Remove whitespaces
    	for(int i = 0; i < tokens.size(); i++) {
    		if(tokens.get(i).matches("\\s")) {
    			tokens.remove(i);
    			i = i -1;
    		}
    	}
    	
    	//Deal with unary operators
    	for(int i = 0; i < tokens.size(); i++) {
    		if(tokens.get(i).matches("-")) {
    			if(i == 0) {
    				tokens.set(i+1, "-"+tokens.get(i+1));
    				tokens.remove(i);
    			}else if(tokens.get(i-1).matches("\\*")
    					|| tokens.get(i-1).matches("\\+")
    					|| tokens.get(i-1).matches("/")
    					|| tokens.get(i-1).matches("-")) {
    				tokens.set(i+1, "-"+tokens.get(i+1));
    				tokens.remove(i);
    			}else if(tokens.get(i-1).matches("-?\\d+(\\.\\d+)(E)")) {
    				tokens.set(i-1, tokens.get(i-1)+"-"+tokens.get(i+1));
    				tokens.remove(i);
    				tokens.remove(i);
    			}
    		}
    	}
    	
    	//Iterates within parenthesis
    	for(int i = 0; i < tokens.size(); i++) {
    		if(tokens.get(i).matches("\\(") || tokens.get(i).matches("\\[")) {
    			par.push(i);
    		}else if(!par.isEmpty() && tokens.get(i).matches("\\)") && tokens.get(par.peek()).matches("\\(")) {
    			int begin = (int) par.pop();
    			String tmp = "";
    			for(int k = begin+1; k < i; k++) {
    				tmp += tokens.get(k);
    			}
    			float inres = evaluate(tmp, vars, arrays);
//    			System.out.println(inres);
    			//Replace parenthesis with value of what's inside
    			for(int j = begin; j < i; j++) {
    				tokens.remove(j);
    				j--;
    				i--;
    			}
    			tokens.set(i, String.valueOf(inres));
    			expr="";
    			for(String c: tokens) {
    				expr += c;
    			}
    			
//    			System.out.println(expr);
//    			for(String str: tokens) {
//    				System.out.print(str);
//    			}
//    			System.out.println("");
    			
    		}else if(!par.isEmpty() && tokens.get(i).matches("]") && tokens.get(par.peek()).matches("\\[")) {
    			int begin = (int) par.pop();
    			String tmp = "";
    			for(int k = begin+1; k < i; k++) {
    				tmp += tokens.get(k);
    			}
    			float inres = evaluate(tmp, vars, arrays);
    			//Replace parenthesis with value of what's inside
    			int index = (int) inres;
    			int arrVal = 0;
    			for(Array arr: arrays) {
    				if(arr.name.matches(tokens.get(begin-1))) {
    					arrVal = arr.values[index];
    				}
    			}
    			for(int j = begin; j < i; j++) {
    				tokens.remove(j);
    				j--;
    				i--;
    			}
    			tokens.remove(i);
    			i = i-1;
    			tokens.set(begin-1, String.valueOf(arrVal));
    			expr="";
    			for(String c: tokens) {
    				expr += c;
    			}
    		}
    	}
    	
    	//Evaluates each expression
    	for(int i = tokens.size()-1; i >= 0; i--) {
    		for(Variable var: vars) {
    			if(!tokens.get(i).matches("\\+") && !tokens.get(i).matches("\\*") && !tokens.get(i).matches("\\(") 
    			&& !tokens.get(i).matches("\\[") && var.name.matches(tokens.get(i))) {
    				values.push((float) var.value);
    				continue;
    			}
    		}
    		if(tokens.get(i).matches("-?\\d+(\\.\\d+)?(E-?\\d+)?")) {
				values.push((float)Float.parseFloat(tokens.get(i)));
				continue;
			}
    		if((tokens.get(i).matches("\\*")
    			|| tokens.get(i).matches("/")
    			|| tokens.get(i).matches("\\+")
    			|| tokens.get(i).matches("-")) && operators.isEmpty()) {
    			operators.push(tokens.get(i));
    			continue;
    		}else if((tokens.get(i).matches("\\+")
    				|| tokens.get(i).matches("-")) && !operators.isEmpty()) {
    			while(!operators.isEmpty() && (operators.peek().matches("\\*") || operators.peek().matches("/"))) {
	    			float first = values.pop();
	    			float second = values.pop();
	    			if(operators.peek().matches("\\*")) {
	    				float third = first * second;
	    				values.push(third);
	    				operators.pop();
	    			}else if(operators.peek().matches("/")) {
	    				float third = first / second;
	    				values.push(third);
	    				operators.pop();
	    			}else if(operators.peek().matches("\\+")) {
	    				float third = first + second;
	    				values.push(third);
	    				operators.pop();
	    			}else if(operators.peek().matches("-")) {
	    				float third = first - second;
	    				values.push(third);
	    				operators.pop();
	    			}
    			}
    			//operators.pop();
    			operators.push(tokens.get(i));
    			continue;
    		}else if((tokens.get(i).matches("\\*")
    				|| tokens.get(i).matches("/")) && !operators.isEmpty()) {
    			operators.push(tokens.get(i));
    			continue;
    		}
    	}
    	
//    	System.out.println(expr);
//    	System.out.println("Operators: " + operators.size());
//    	System.out.println("Values: " + values.size());
    	
    	while(!operators.isEmpty()) {
    		float first = values.pop();
    		float second = values.pop();
    		if(operators.peek().matches("\\*")) {
				float third = first * second;
				values.push(third);
			}else if(operators.peek().matches("/")) {
				float third = first / second;
				values.push(third);
			}else if(operators.peek().matches("\\+")) {
				float third = first + second;
				values.push(third);
			}else if(operators.peek().matches("-")) {
				float third = first - second;
				values.push(third);
			}
    		operators.pop();
    	}
    	
    	if(values.size() == 1) {
    		res = values.pop();
    	}
    	
    	return res;
    	
    	
    }
}
