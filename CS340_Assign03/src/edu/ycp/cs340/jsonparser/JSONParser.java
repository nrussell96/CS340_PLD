package edu.ycp.cs340.jsonparser;

public class JSONParser {
	private Lexer lexer;
	
	public JSONParser(Lexer lexer) {
		this.lexer = lexer;
	}
	
	public Node parseValue() {
		Node value = new Node(Symbol.VALUE);
		
		Token tok = lexer.peek();
		
		if (tok == null) {
			throw new ParserException("Unexpected end of input reading value");
		}
		
		if (tok.getSymbol() == Symbol.STRING_LITERAL) {
			// Value â†’ StringLiteral
			value.getChildren().add(expect(Symbol.STRING_LITERAL));
		} else if (tok.getSymbol() == Symbol.INT_LITERAL) {
			// Value â†’ IntLiteral
			value.getChildren().add(expect(Symbol.INT_LITERAL));
		} else if (tok.getSymbol() == Symbol.LBRACE) {
			// Value â†’ Object
			value.getChildren().add(parseObject());
		} else if (tok.getSymbol() == Symbol.LBRACKET) {
			// Value â†’ Array
			value.getChildren().add(parseArray());
		} else {
			throw new ParserException("Unexpected token looking for value: " + tok);
		}
		
		return value;
	}

	private Node parseObject() {
		Node object = new Node(Symbol.OBJECT);
		
		// Object â†’ "{" OptFieldList "}"
		object.getChildren().add(expect(Symbol.LBRACE));
		object.getChildren().add(parseOptFieldList());
		object.getChildren().add(expect(Symbol.RBRACE));
		
		return object;
	}
	
	private Node parseField(){
		Node field = new Node(Symbol.FIELD);
		// Field:: StringLiteral “:” Value
		field.getChildren().add(expect(Symbol.STRING_LITERAL));
		field.getChildren().add(expect(Symbol.COLON));
		field.getChildren().add(parseValue());
		
		return field;
	}

	private Node parseOptFieldList() {
		Node optFieldList = new Node(Symbol.OPT_FIELD_LIST);
		Token tok = lexer.peek();
		
		if (tok == null) {
			throw new ParserException("Unexpected end of input reading value");
		}
		
		if(tok.getSymbol() == Symbol.RBRACE){
			//epsilon
		}else{
			optFieldList.getChildren().add(parseFieldList());
		}
		return optFieldList;
	}
	
	private Node parseFieldList(){
		Node fieldList = new Node(Symbol.FIELD_LIST);
		Token tok = lexer.peek();

		if (tok == null) {
			throw new ParserException("Unexpected end of input reading value");
		}
		
		//FieldList:: Field | Field “,” FieldList
		fieldList.getChildren().add(parseField());
		if(tok.getSymbol() == Symbol.COMMA){
			fieldList.getChildren().add(new Node(lexer.next()));
			fieldList.getChildren().add(parseFieldList());
		}
		return fieldList;
	}
	private Node parseArray() {
		Node array = new Node(Symbol.ARRAY);
		
		//Array:: “[” OptValueList “]”
		array.getChildren().add(expect(Symbol.LBRACKET));
		array.getChildren().add(parseOptValueList());
		array.getChildren().add(expect(Symbol.RBRACKET));
		
		return array;
	}
	
	private Node parseOptValueList() {
		Node optValueList = new Node(Symbol.OPT_VALUE_LIST);
		Token tok = lexer.peek();
		
		if (tok == null) {
			throw new ParserException("Unexpected end of input reading value");
		}
		
		//OptValueList:: ValueList | epsilon
		if(tok.getSymbol() == Symbol.RBRACKET){
			//epsilon
		}else{
			optValueList.getChildren().add(parseValueList());
		}
		return optValueList;	
	}

	private Node parseValueList() {
		Node valueList = new Node(Symbol.VALUE_LIST);
		Token tok = lexer.peek();
		
		if (tok == null) {
			throw new ParserException("Unexpected end of input reading value");
		}
		
		//ValueList:: Value | Value “,” ValueList
		valueList.getChildren().add(parseValue());
		if(tok.getSymbol() == Symbol.COMMA){
			valueList.getChildren().add(new Node(lexer.next()));
			valueList.getChildren().add(parseValueList());
		}
		return valueList;
	}
	private Node expect(Symbol symbol) {
		Token tok = lexer.next();
		if (tok.getSymbol() != symbol) {
			throw new LexerException("Unexpected token " + tok + " (was expecting " + symbol + ")");
		}
		return new Node(tok);
	}
}
