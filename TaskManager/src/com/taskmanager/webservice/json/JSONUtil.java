/*
 * $Id: JSONUtil.java 1055126 2011-01-04 18:14:46Z mcucchiara $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.taskmanager.webservice.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;




public class JSONUtil {
    
    final static String RFC3339_FORMAT = "yyyy-MM-dd-HH:mm";
    
    

    public static Object deserialize(String json) throws JSONException {
        JSONReader reader = new JSONReader();
        return reader.read(json);
    }

    /**
     * Deserializes a object from JSON
     * 
     * @param reader
     *            Reader to read a JSON string from
     * @return deserialized object
     * @throws JSONException
     *             when IOException happens
     */
    public static Object deserialize(Reader reader) throws JSONException {
        // read content
        BufferedReader bufferReader = new BufferedReader(reader);
        String line;
        StringBuilder buffer = new StringBuilder();

        try {
            while ((line = bufferReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            throw new JSONException();
        }

        return deserialize(buffer.toString());
    }

    
    public static List<String> asList(String commaDelim) {
        if ((commaDelim == null) || (commaDelim.trim().length() == 0))
            return null;
        List<String> list = new ArrayList<String>();
        String[] split = commaDelim.split(",");
        for (int i = 0; i < split.length; i++) {
            String trimmed = split[i].trim();
            if (trimmed.length() > 0) {
                list.add(trimmed);
            }
        }
        return list;
    }

   
    /**
     * Realizes the visit(Class) method called by vistInterfaces for all
     * encountered classes/interfaces
     */
    public static interface ClassVisitor {

        /**
         * Called when a new interface/class is encountered
         * 
         * @param aClass
         *            the encountered class/interface
         * @return true if the recursion should continue, false to stop
         *         recursion immediately
         */
        @SuppressWarnings("unchecked")
        boolean visit(Class aClass);
    }

    /**
     * Visit all the interfaces realized by the specified object, its
     * superclasses and its interfaces <p/> Visitation is performed in the
     * following order: aClass aClass' interfaces the interface's superclasses
     * (interfaces) aClass' superclass superclass' interfaces superclass'
     * interface's superclasses (interfaces) super-superclass and so on <p/> The
     * Object base class is base excluded. Classes/interfaces are only visited
     * once each
     * 
     * @param aClass
     *            the class to start recursing upwards from
     * @param visitor
     *            this vistor is called for each class/interface encountered
     * @return true if all classes/interfaces were visited, false if it was
     *         exited early as specified by a ClassVisitor result
     */
    @SuppressWarnings("unchecked")
    public static boolean visitInterfaces(Class aClass, ClassVisitor visitor) {
        List<Class> classesVisited = new LinkedList<Class>();
        return visitUniqueInterfaces(aClass, visitor, classesVisited);
    }

    /**
     * Recursive method to visit all the interfaces of a class (and its
     * superclasses and super-interfaces) if they haven't already been visited.
     * <p/> Always visits itself if it hasn't already been visited
     * 
     * @param thisClass
     *            the current class to visit (if not already done so)
     * @param classesVisited
     *            classes already visited
     * @param visitor
     *            this vistor is called for each class/interface encountered
     * @return true if recursion can continue, false if it should be aborted
     */
    private static boolean visitUniqueInterfaces(Class thisClass, ClassVisitor visitor,
            List<Class> classesVisited) {
        boolean okayToContinue = true;

        if (!classesVisited.contains(thisClass)) {
            classesVisited.add(thisClass);
            okayToContinue = visitor.visit(thisClass);

            if (okayToContinue) {
                Class[] interfaces = thisClass.getInterfaces();
                int index = 0;
                while ((index < interfaces.length) && (okayToContinue)) {
                    okayToContinue = visitUniqueInterfaces(interfaces[index++], visitor, classesVisited);
                }

                if (okayToContinue) {
                    Class superClass = thisClass.getSuperclass();
                    if ((superClass != null) && (!Object.class.equals(superClass))) {
                        okayToContinue = visitUniqueInterfaces(superClass, visitor, classesVisited);
                    }
                }
            }
        }
        return okayToContinue;
    }

    public static  <T> T getJavaObject(String json,Class<T> classObject) throws Exception

    {
    	T valueObject=classObject.newInstance();
    	Object objectMap=deserialize(json);
    	JSONPopulator populator=new JSONPopulator();
    	populator.populateObject(valueObject, (Map)objectMap);
    	
    	return valueObject;
    }
    
    
    
}
