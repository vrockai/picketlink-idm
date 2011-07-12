package org.picketlink.idm.impl.configuration.stax;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.picketlink.idm.spi.configuration.metadata.IdentityObjectAttributeMetaData;
import org.staxnav.Naming;
import org.staxnav.StaxNavigatorImpl;

/**
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
public class ParserUtilTestCase extends TestCase
{

    /** . */
   private StaxNavigatorImpl<String> navigator;

   @Override
   protected void setUp() throws Exception
   {

   }

   public void testOptions() throws Exception
   {
//      InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("config/parse-options.xml");
//      XMLInputFactory factory = XMLInputFactory.newInstance();
//      XMLStreamReader stream = factory.createXMLStreamReader(is);
//
//      //
//      navigator = new StaxNavigatorImpl<String>(new Naming.Local(), stream);
//
//
//      assertTrue(navigator.child("options"));
//      Map<String, List<String>> options = ParserUtil.parseOptions(navigator);
//
//      assertEquals(3, options.size());
//      assertEquals(4, options.get("opt-c").size());




   }

   public void testAttributess() throws Exception
   {

//      InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("config/parse-attributes.xml");
//      XMLInputFactory factory = XMLInputFactory.newInstance();
//      XMLStreamReader stream = factory.createXMLStreamReader(is);
//
//      //
//      navigator = new StaxNavigatorImpl<String>(new Naming.Local(), stream);
//
//
//      assertTrue(navigator.child("attributes"));
//      List<IdentityObjectAttributeMetaData> attrs = ParserUtil.parseAttributes(navigator);
//
//      assertEquals(3, attrs.size());
//
//      IdentityObjectAttributeMetaData attr = attrs.get(0);
//      assertEquals("picture", attr.getName());
//      assertEquals("user.picture", attr.getStoreMapping());
//      assertEquals("binary", attr.getType());
//      assertEquals(false, attr.isReadonly());
//
//      attr = attrs.get(1);
//      assertEquals("email", attr.getName());
//      assertEquals("mail", attr.getStoreMapping());
//      assertEquals("text", attr.getType());
//      assertEquals(false, attr.isReadonly());
//
//      attr = attrs.get(2);
//      assertEquals("description", attr.getName());
//      assertEquals("description", attr.getStoreMapping());
//      assertEquals("text", attr.getType());
//      assertEquals(true, attr.isReadonly());

   }
}
