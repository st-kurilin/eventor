
package com.aggregator;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1 in JDK 6
 * Generated source version: 2.1
 */
@WebServiceClient(name = "HandlingReportServiceService", targetNamespace = "http://ws.handling.interfaces.dddsample.citerus.se/", wsdlLocation = "file:///Users/peter/src/dddsample/src/main/resources/HandlingReportService.wsdl")
public class HandlingReportServiceService
        extends Service {

    private final static URL HANDLINGREPORTSERVICESERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("file:/Users/peter/src/dddsample/src/main/resources/HandlingReportService.wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HANDLINGREPORTSERVICESERVICE_WSDL_LOCATION = url;
    }

    public HandlingReportServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public HandlingReportServiceService() {
        super(HANDLINGREPORTSERVICESERVICE_WSDL_LOCATION, new QName("http://ws.handling.interfaces.dddsample.citerus.se/", "HandlingReportServiceService"));
    }

    /**
     * @return returns HandlingReportService
     */
    @WebEndpoint(name = "HandlingReportServicePort")
    public HandlingReportService getHandlingReportServicePort() {
        return (HandlingReportService) super.getPort(new QName("http://ws.handling.interfaces.dddsample.citerus.se/", "HandlingReportServicePort"), HandlingReportService.class);
    }

    /**
     * @param features A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return returns HandlingReportService
     */
    @WebEndpoint(name = "HandlingReportServicePort")
    public HandlingReportService getHandlingReportServicePort(WebServiceFeature... features) {
        return (HandlingReportService) super.getPort(new QName("http://ws.handling.interfaces.dddsample.citerus.se/", "HandlingReportServicePort"), HandlingReportService.class, features);
    }

}
