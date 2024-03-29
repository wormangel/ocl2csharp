package xmiParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmiParser {
	
	// Nomes em C# para termos de UML
	
	public static final String VISIBILITY_PUBLIC = "public";
	public static final String VISIBILITY_PRIVATE = "private";
	public static final String VISIBILITY_PROTECTED = "protected";
	public static final String VISIBILITY_PACKAGE = "internal";
	
	// Tipos C#
	public static final String TYPE_VOID = "void";
	public static final String TYPE_INT = "int";
	public static final String TYPE_STRING = "string";
	public static final String TYPE_BOOLEAN = "bool";
	
	// Tipos OCL
	public static final String RETURN_TYPE_VOID = "void";
	public static final String RETURN_TYPE_INT = "integer";
	public static final String RETURN_TYPE_STRING = "string";
	public static final String RETURN_TYPE_BOOLEAN = "boolean";
	private static final String RETURN_TYPE_COLLECTION = "collection";

	private final static String XMI_PATH = "test/xmi/profe.uml2";
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{

		for (DomainClass c : fullParse()) {
			System.out.println(c);
		}
		
		System.out.println(isValidOperation("ProgramaFidelidade", "cadastrar(d:integer)","boolean"));

	}
	
	
	public static List<DomainClass> fullParse() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		List<DomainClass> list = new ArrayList<DomainClass>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class']"));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			ArrayList<String> resultado = new ArrayList<String>();
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				for(int i = 0; i < nodes.getLength(); i++){
					
					if(nodes.item(i).getAttributes().getNamedItem("name") != null) {
						String className = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
						DomainClass classe = new DomainClass(className);
						classe.setOperations(parseClassOperations(className));
						classe.setAttributes(parseClassAttributes(className));
						if(isSubclass(className)) {
							classe.setSuperClass(getSuperclassName(className));
						}
						
						list.add(classe);
					}					
				}
		}
		
		return list;
	}
	
	public static boolean isSubclass(String className) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/generalization", className));
		Object result = expr.evaluate(doc, XPathConstants.NODE);
		if (result != null){
			Node node = (Node) result;
			if (node != null) {
				return true;	
			}
		}
		return false;
	}
	
	public static String getSuperclassName(String className) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/generalization", className));
		Object result = expr.evaluate(doc, XPathConstants.NODE);
		if (result != null){
			Node node = (Node) result;
			if (node != null) {
				String idSuperclass;
				if(node.getAttributes().getNamedItem("general") != null){
					idSuperclass = node.getAttributes().getNamedItem("general").getNodeValue();	
					return getClassName(idSuperclass);
				}
			}
		}
		return null;
	}
	

	
	public static List<Enumerator> parseEnums() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		ArrayList<Enumerator> resultado = new ArrayList<Enumerator>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Enumeration']"));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				for(int i = 0; i < nodes.getLength(); i++){
					if(nodes.item(i).getAttributes().getNamedItem("name") != null) {
						
						String enumName = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
						expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Enumeration'][@name='%s']/ownedLiteral", enumName));
						Object resultAux = expr.evaluate(doc, XPathConstants.NODESET);
						
						Enumerator en = new Enumerator(enumName);
						
						if(resultAux != null){
							List<String> values = new ArrayList<String>();
							NodeList nodesAux = (NodeList) resultAux;
							for(int j = 0; j < nodesAux.getLength(); j++){
								values.add(nodesAux.item(j).getAttributes().getNamedItem("name").getNodeValue());
							}
						en.setItens(values);	
						}
						resultado.add(en);
					}					
				}
			return resultado;
		}
		
		
		return resultado;
	}
	
	public static List<Attribute> parseClassAttributes(String className) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		ArrayList<Attribute> resultado = new ArrayList<Attribute>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedAttribute", className));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0){
				
				// Para cada atributo
				for(int i = 0; i < nodes.getLength(); i++){
					
					if(nodes.item(i).getAttributes().getNamedItem("name") != null) {
						String attributeName = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
						String visibility = "";
						if(nodes.item(i).getAttributes().getNamedItem("visibility") != null){
							visibility = nodes.item(i).getAttributes().getNamedItem("visibility").getNodeValue();
						}
						String type;
						if(nodes.item(i).getAttributes().getNamedItem("type") != null){
							type = getClassName(nodes.item(i).getAttributes().getNamedItem("type").getNodeValue());
						} else {
							Node nodoTipo = (Node) nodes.item(i).getFirstChild().getNextSibling();
							type = getPrimitiveTypeName(nodoTipo.getAttributes().getNamedItem("href").getNodeValue());
						}
						
						Attribute att = new Attribute(attributeName, type);
						att.setVisibility(getVisibilityName(visibility));
						resultado.add(att);
					}					
				}
			}
		}
		return resultado;				
	}
	
	private static String getVisibilityName(String v){
		if(v.equals("public")){
			return VISIBILITY_PUBLIC;
		} else if (v.equals("private")){
			return VISIBILITY_PRIVATE;
		} else if (v.equals("protected")){
			return VISIBILITY_PROTECTED;
		} else return VISIBILITY_PACKAGE;
	}
	
	public static List<Operation> parseClassOperations(String className) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		ArrayList<Operation> resultado = new ArrayList<Operation>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedOperation", className));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0){
				
				// Para cada opera��o
				for(int i = 0; i < nodes.getLength(); i++){
					
					if(nodes.item(i).getAttributes().getNamedItem("name") != null) {
						String operationName = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
						expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedOperation[@name='%s']/returnResult", 
								className, operationName));
						Object resultAux = expr.evaluate(doc, XPathConstants.NODE);
						String returnType;
						
						// Se a opera��o retorna algo
						if (resultAux != null){
							Node node = (Node) resultAux;
							// Se o retorno � um atributo da tag XML
							if(node.getAttributes().getNamedItem("type") != null){
								returnType = getClassName(node.getAttributes().getNamedItem("type").getNodeValue());
							} else { // Se nao, se est� no nodo filho
								Node nodoTipo = (Node) node.getFirstChild().getNextSibling();
								returnType = getPrimitiveTypeName(nodoTipo.getAttributes().getNamedItem("href").getNodeValue());
							}
						} else {
							returnType = TYPE_VOID;
						}
						
						// Parametros
						
						expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedOperation[@name='%s']/ownedParameter", 
								className, operationName));
						resultAux = expr.evaluate(doc, XPathConstants.NODESET);
						
						// Se a opera��o tem parametros
						ArrayList<OperationParameter> listaParams = new ArrayList<OperationParameter>();
						if (resultAux != null){
							NodeList nodesParams = (NodeList) resultAux;
							if (nodesParams.getLength() > 0){
								// Para cada parametro
								for(int j = 0; j < nodesParams.getLength(); j++){
									if(nodesParams.item(j).getAttributes().getNamedItem("name") != null) {
										String paramName = nodesParams.item(j).getAttributes().getNamedItem("name").getNodeValue();
										String paramType;
										if(nodesParams.item(j).getAttributes().getNamedItem("type") != null){
											paramType = getClassName(nodesParams.item(j).getAttributes().getNamedItem("type").getNodeValue());
										} else {
											Node nodoTipo = (Node) nodesParams.item(j).getFirstChild().getNextSibling();
											paramType = getPrimitiveTypeName(nodoTipo.getAttributes().getNamedItem("href").getNodeValue());
										}
										
										OperationParameter param = new OperationParameter(paramName, paramType);
										listaParams.add(param);
									}
								}
							}
						}
						
						Operation operation = new Operation(operationName, returnType);	
						operation.setParameters(listaParams);
						resultado.add(operation);
					}					
				}
			}
		}
		return resultado;				
	}

	
	public static List<String> listClasses() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class']"));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			ArrayList<String> resultado = new ArrayList<String>();
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				for(int i = 0; i < nodes.getLength(); i++){
					if(nodes.item(i).getAttributes().getNamedItem("name") != null) {
						resultado.add(nodes.item(i).getAttributes().getNamedItem("name").getNodeValue());	
					}					
				}
			return resultado;
		}
		return null;
	}
	
	public static String getClassName(String id) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@xmi:id='%s']", id));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0) {
				if(nodes.item(0).getAttributes().getNamedItem("name") != null){
					return nodes.item(0).getAttributes().getNamedItem("name").getNodeValue();	
				}
			} 
		}		
		
		expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Enumeration'][@xmi:id='%s']", id));
		result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0) {
				if(nodes.item(0).getAttributes().getNamedItem("name") != null){
					return nodes.item(0).getAttributes().getNamedItem("name").getNodeValue();
				}
			} 
		}		
		return null;
	}
	
	public static List<String> getAttributesNames(String classe) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedAttribute", classe));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			ArrayList<String> resultado = new ArrayList<String>();
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				for(int i = 0; i < nodes.getLength(); i++){
					if(nodes.item(i).getAttributes().getNamedItem("name") != null) {
						resultado.add(nodes.item(i).getAttributes().getNamedItem("name").getNodeValue());	
					}					
				}
			return resultado;
		}
		return null;
	}

	public static List<String> getOperationsNames(String classe) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedOperation", classe));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			ArrayList<String> resultado = new ArrayList<String>();
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				for(int i = 0; i < nodes.getLength(); i++){
					if(nodes.item(i).getAttributes().getNamedItem("name") != null) {
						resultado.add(nodes.item(i).getAttributes().getNamedItem("name").getNodeValue());	
					}					
				}
			return resultado;
		}
		return null;
	}
	
	public static String getPrimitiveTypeName(String id){
		if (id.equals("pathmap://UML2_LIBRARIES/UML2PrimitiveTypes.library.uml2#_IXlH8a86EdieaYgxtVWN8Q")){
			return TYPE_STRING;
		} else if (id.equals("pathmap://UML2_LIBRARIES/UML2PrimitiveTypes.library.uml2#_IXfBUK86EdieaYgxtVWN8Q")){
			return TYPE_BOOLEAN;
		} else if (id.equals("pathmap://UML2_LIBRARIES/UML2PrimitiveTypes.library.uml2#_IXlH8K86EdieaYgxtVWN8Q")){
			return TYPE_INT;
		} else return null;
	}
	
	public static boolean isValidClass(String className) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']", className));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				return true;
		}
		return false;
	}
	
	public static boolean isValidAttribute(String className, String attribute) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedAttribute[@name='%s']", className, attribute));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				return true;
		}
		return false;
	}
	
	// Metodos que NAO recebem parametros
	public static boolean isValidOperation(String className, String operation) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedOperation[@name='%s']", className, operation));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				return true;
		}
		return false;
	}
	
	// operationCall sempre deve ser invocado com parenteses!
	// Exemplo: isValidOperation("ProgramaFidelidade", "cadastra(c:Cliente)", "boolean")
	public static boolean isValidOperation(String className, String operationCall, String returnType) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		
		String xPathExpr;
		
		int indexLeftParen = operationCall.indexOf("(");
		int indexRightParen = operationCall.indexOf(")");
		String operation = operationCall.substring(0,indexLeftParen);
		
		String parameters = operationCall.substring(indexLeftParen+1, indexRightParen);

		// Se nao tiver parametros
		if (parameters.isEmpty()){
			xPathExpr = String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedOperation[@name='%s']", className, operation);
			XPathExpression expr = xpath.compile(xPathExpr);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			if (result != null){
				NodeList nodes = (NodeList) result;
				if (nodes.getLength() <= 0) { // se nao tem metodo com esse nome
					return false;
				} else { // se tiver metodo com esse nome
					
					int quantOverloadsComParametros = 0;
					int quantOverloads = nodes.getLength();
					
					for(int i = 0; i < nodes.getLength(); i++){ // pra cada metodo com esse nome
						
						Node nohAtual = nodes.item(i);
						
						for (int j = 0; j < nohAtual.getChildNodes().getLength(); j++){ // verifica se recebe parametros
							NodeList nohsFilhos = nohAtual.getChildNodes();
							if(nohsFilhos.item(j).getNodeName().equals("ownedParameter")) {
								quantOverloadsComParametros++;
								break;
							}	
						}
					}
					
					if (quantOverloadsComParametros == quantOverloads) { // se todos os overloads recebem parametro, eh invalido
						return false;
					}
				}
			}
		} else { //Se tiver parametros
			// Separa os parametros
			for (String parameter : parameters.split(",")) {
				xPathExpr = String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedOperation[@name='%s']/ownedParameter", className, operation);
				String paramName = parameter.split(":")[0];
				String paramType = parameter.split(":")[1];
				
				xPathExpr += String.format("[@name='%s']", paramName );
				xPathExpr += String.format("[@type='%s']", getXmiNameForType(paramType) );
				
				XPathExpression expr = xpath.compile(xPathExpr);
				Object result = expr.evaluate(doc, XPathConstants.NODESET);
				if (result != null){
					NodeList nodes = (NodeList) result;
					if (nodes.getLength() <= 0)
						return false;
				}
			}	
		}
		return true;
	}
	
	// Retorna o nome gerado pelo XMI para um determinado tipo
	// Exemplo: getXmiNameForType("Conta") = "_fx7WemPXEd-bkL5iYhiD_Q"
	public static String getXmiNameForType(String type) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
		
		if (type.equals(RETURN_TYPE_INT)){
			return "pathmap://UML2_LIBRARIES/UML2PrimitiveTypes.library.uml2#_IXlH8K86EdieaYgxtVWN8Q";
		} else if (type.equals(RETURN_TYPE_BOOLEAN)){
			return "pathmap://UML2_LIBRARIES/UML2PrimitiveTypes.library.uml2#_IXlH8K86EdieaYgxtVWN8Q";
		} else if (type.equals(RETURN_TYPE_STRING)){
			return "pathmap://UML2_LIBRARIES/UML2PrimitiveTypes.library.uml2#_IXlH8a86EdieaYgxtVWN8Q";
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']", type));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0) {
				if(nodes.item(0).getAttributes().getNamedItem("xmi:id") != null){
					return nodes.item(0).getAttributes().getNamedItem("xmi:id").getNodeValue();	
				}
			} 
		}		
		
		expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Enumeration'][@name='%s']", type));
		result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0) {
				if(nodes.item(0).getAttributes().getNamedItem("xmi:id") != null){
					return nodes.item(0).getAttributes().getNamedItem("xmi:id").getNodeValue();
				}
			} 
		}		
		return null;
	}
	
	// Espera receber uma expressao como Cliente.programa.parceiros e verifica se eh valida, navegavel
	// Provavelmente nao funciona com metodos
	public static boolean isValidPath(String context, String path) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		
		if (!isValidClass(context)) return false;	
		
		// Separa string por pontos
		StringTokenizer tokenizer = new StringTokenizer(path, ".");
		
		String[] ids = new String[tokenizer.countTokens()];
		
		for(int i = 0; i <= tokenizer.countTokens(); i++){
			ids[i] = tokenizer.nextToken();
		}
		
		String source = context;
		String destination;
		
		for(int i = 0; i < ids.length; i++){
			destination = ids[i];
			if (!isValidStep(source, destination)){
				return false;
			}
			source = getAssociationName(context,destination);
		}
		return true;
		
	}
	
	private static boolean isValidStep(String context, String destination) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException{
		boolean resultAttribute = isValidAttribute(context, destination);
		boolean resultOperation = isValidOperation(context, destination);
		boolean resultAssociation = isValidAssociation(context, destination);
		
		return resultAttribute || resultOperation || resultAssociation;
	}

	private static boolean isValidAssociation(String context, String destination) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedAttribute[@name='%s'][@association!='null']", context, destination));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				return true;
		}
		return false;
	}
	
	// Recupera o nome da classe de um atributo-associacao (parceiros - ParceiroPrograma, por exemplo) ou retorna o nome do atributo como foi passado se a dada
	// classe nao for associacao
	// Nao deve ser invocado diretamente, apenas no local esperado (nao faz checagem de existencia de classe - propenso a erros)
	public static String getAssociationName(String context, String attribute) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {

		
		if(!isValidAssociation(context, attribute))
			return attribute;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		XPathExpression expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedAttribute[@name='%s']", context, attribute));
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		
		String idAssociation = "";
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				idAssociation = nodes.item(0).getAttributes().getNamedItem("association").getNodeValue();
		}
		
		String idContext = "";
		expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']", context, attribute));
		result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				idContext = nodes.item(0).getAttributes().getNamedItem("xmi:id").getNodeValue();
		}
		
		String associationName = "";
		expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class']/ownedAttribute[@type='%s'][@association='%s']", idContext, idAssociation));
		result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null){
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0)
				associationName = nodes.item(0).getParentNode().getAttributes().getNamedItem("name").getNodeValue();
		}
		
		return associationName;
	}


	// Retorna o tipo de um atributo. S� vale para tipos simples.
	public static String getType(String currentContext, String property) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		
		if(!isValidPath(currentContext, property)){
			return null;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(XMI_PATH);		
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new PersonalNamespaceContext());
		
		XPathExpression expr;
		
		Object result = null;
		
		if (isValidAttribute(currentContext, property)){
			expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedAttribute[@name='%s']", currentContext, property));
			result = expr.evaluate(doc, XPathConstants.NODE);
		} else if (isValidOperation(currentContext, property)){
			expr = xpath.compile(String.format("//ownedMember[@xmi:type='uml:Class'][@name='%s']/ownedOperation[@name='%s']/returnResult", currentContext, property));
			result = expr.evaluate(doc, XPathConstants.NODE);
		}
		
		if (result != null){
			Node node = (Node) result;
			if (node != null){
				String type;
				
				// Cole��o
				if(node.hasChildNodes()){
					Node filho = node.getFirstChild().getNextSibling();
					if(filho != null){
						if (filho.getAttributes().getNamedItem("xmi:type").getNodeValue().equals("uml:LiteralUnlimitedNatural")){
							if(Integer.parseInt(filho.getAttributes().getNamedItem("value").getNodeValue()) == -1){
								return RETURN_TYPE_COLLECTION;
							}
						}
					}
				}
				
				if(node.getAttributes().getNamedItem("type") != null){
					type = getClassName(node.getAttributes().getNamedItem("type").getNodeValue());
				} else {
					Node filho = node.getFirstChild().getNextSibling();
					type = getPrimitiveTypeName(filho.getAttributes().getNamedItem("href").getNodeValue());
				}
				
				if (type.equals(TYPE_INT)){
					return RETURN_TYPE_INT;
				} else if (type.equals(TYPE_BOOLEAN)){
					return RETURN_TYPE_BOOLEAN;
				} else if (type.equals(TYPE_STRING)){
					return RETURN_TYPE_STRING;
				} else {
					return type;
				}
			}
		}
		return null;
	}
	
}
