/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor  license  agreements.  See the NOTICE file distributed
 * with this work  for  additional  information  regarding  copyright
 * ownership.  The ASF  licenses  this file to you under  the  Apache
 * License, Version 2.0 (the "License"); you may not  use  this  file
 * except in compliance with the License.  You may obtain  a copy  of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless  required  by  applicable law  or  agreed  to  in  writing,
 * software  distributed  under  the  License  is  distributed  on an
 * "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR  CONDITIONS  OF ANY KIND,
 * either  express  or implied.  See  the License  for  the  specific
 * language governing permissions and limitations under  the License.
 */
package org.apache.clerezza.rdf.jena.parser;

import org.apache.clerezza.IRI;
import org.apache.clerezza.rdf.jena.facade.JenaGraph;
import org.apache.clerezza.representation.ParsingProvider;
import org.apache.clerezza.representation.SupportedFormat;
import org.apache.clerezza.representation.UnsupportedParsingFormatException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.osgi.service.component.annotations.Component;

import java.io.InputStream;

/**
 * A {@link org.apache.clerezza.representation.ParsingProvider} based on Jena
 *
 * @author reto, mir
 */
@Component(
    name="Jena Parser Provider",
    service=ParsingProvider.class,
    property = {
        "supportedFormat={" +
            "SupportedFormat.RDF_XML," +
            "SupportedFormat.TURTLE," +
            "SupportedFormat.X_TURTLE," +
            "SupportedFormat.N_TRIPLE," +
            "SupportedFormat.N3," +
            "\"application/ld+json\"" +
        "}"
    },
    immediate=true
)
@SupportedFormat({
    SupportedFormat.RDF_XML,
    SupportedFormat.TURTLE,
    SupportedFormat.X_TURTLE,
    SupportedFormat.N_TRIPLE,
    SupportedFormat.N3,
    "application/ld+json"
})
public class JenaParserProvider implements ParsingProvider {

    public void parse(org.apache.clerezza.Graph target, InputStream serializedGraph, String formatIdentifier, IRI baseUri) {
        String jenaFormat = getJenaFormat(formatIdentifier);
        org.apache.jena.graph.Graph graph = new JenaGraph(target);
        Model model = ModelFactory.createModelForGraph(graph);
        String base;
        if (baseUri == null) {
            base = "http://relative-uri.fake/";
        } else {
            base = baseUri.getUnicodeString();
        }
        model.read(serializedGraph, base, jenaFormat);
    }

    private String getJenaFormat(String formatIdentifier) {
        int semicolonPos = formatIdentifier.indexOf(';');
        if (semicolonPos > -1) {
            formatIdentifier = formatIdentifier.substring(0, semicolonPos);
        }
        if (formatIdentifier.equals(SupportedFormat.RDF_XML)) {
            return "RDF/XML-ABBREV";
        }
        if (formatIdentifier.equals(SupportedFormat.TURTLE) ||
                formatIdentifier.equals(SupportedFormat.X_TURTLE)) {
            return "TURTLE";
        }
        if (formatIdentifier.equals(SupportedFormat.N3)) {
            return "N3";
        }
        if (formatIdentifier.equals(SupportedFormat.N_TRIPLE)) {
            return "N-TRIPLE";
        }
        if (formatIdentifier.equals("application/ld+json")) {
            return "JSONLD";
        }
        throw new UnsupportedParsingFormatException(formatIdentifier);
    }
}
