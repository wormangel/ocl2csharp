package xmiParser;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;


public class PersonalNamespaceContext implements NamespaceContext {

	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("xmi".equals(prefix)) return "http://www.omg.org/XMI";
        else if ("uml".equals(prefix)) return "http://www.eclipse.org/uml2/1.0.0/UML";
        return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getPrefixes(String arg0) {
		throw new UnsupportedOperationException();
	}

}
