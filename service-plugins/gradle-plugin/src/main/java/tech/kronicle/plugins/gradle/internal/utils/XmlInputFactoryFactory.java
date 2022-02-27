package tech.kronicle.plugins.gradle.internal.utils;

import javax.xml.stream.XMLInputFactory;

public final class XmlInputFactoryFactory {

    public static XMLInputFactory createXmlInputFactory() {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        return xmlInputFactory;
    }

    private XmlInputFactoryFactory() {
    }
}
