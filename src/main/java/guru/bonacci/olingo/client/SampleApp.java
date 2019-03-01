/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package guru.bonacci.olingo.client;

import static guru.bonacci.olingo.client.Printer.prettyPrint;
import static guru.bonacci.olingo.client.Printer.print;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;


public class SampleApp {

  private ODataClient client;
  private CRUD crud;
  
  public SampleApp() {
    client = ODataClientFactory.getClient();
    crud = new CRUD(client);
  }

  public static void main(String[] params) throws Exception {
    SampleApp app = new SampleApp();
    app.perform("http://localhost:8080/CarService/cars.svc");
  }

  void perform(String serviceUrl) throws Exception {

    print("\n----- Read Edm ------------------------------");
    Edm edm = crud.readEdm(serviceUrl);
    List<FullQualifiedName> ctFqns = new ArrayList<FullQualifiedName>();
    List<FullQualifiedName> etFqns = new ArrayList<FullQualifiedName>();
    for (EdmSchema schema : edm.getSchemas()) {
      for (EdmComplexType complexType : schema.getComplexTypes()) {
        ctFqns.add(complexType.getFullQualifiedName());
      }
      for (EdmEntityType entityType : schema.getEntityTypes()) {
        etFqns.add(entityType.getFullQualifiedName());
      }
    }
    print("Found ComplexTypes", ctFqns);
    print("Found EntityTypes", etFqns);

    print("\n----- Inspect each property and its type of the first entity: " + etFqns.get(0) + "----");
    EdmEntityType etype = edm.getEntityType(etFqns.get(0));
    for (String propertyName : etype.getPropertyNames()) {
      EdmProperty property = etype.getStructuralProperty(propertyName);
      FullQualifiedName typeName = property.getType().getFullQualifiedName();
      print("property '" + propertyName + "' " + typeName);
    }
    
    print("\n----- Read Entities ------------------------------");
    ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator = 
      crud.readEntities(edm, serviceUrl, "Manufacturers");

    while (iterator.hasNext()) {
      ClientEntity ce = iterator.next();
      print("Entry:\n" + prettyPrint(ce.getProperties(), 0));
    }

    print("\n----- Read Entry ------------------------------");
    ClientEntity entry = crud.readEntityWithKey(edm, serviceUrl, "Manufacturers", 1);
    print("Single Entry:\n" + prettyPrint(entry.getProperties(), 0));

    //
    print("\n----- Read Entity with $expand  ------------------------------");
    entry = crud.readEntityWithKeyExpand(edm, serviceUrl, "Manufacturers", 1, "Cars");
    print("Single Entry with expanded Cars relation:\n" + prettyPrint(entry.getProperties(), 0));

    //
    print("\n----- Read Entities with $filter  ------------------------------");
    iterator = crud.readEntitiesWithFilter(edm, serviceUrl, "Manufacturers", "Name eq 'Horse Powered Racing'");
    while (iterator.hasNext()) {
      ClientEntity ce = iterator.next();
      print("Entry:\n" + Printer.prettyPrint(ce.getProperties(), 0));
    }

    // skip everything as odata4 sample/server only supporting retrieval
//    print("\n----- Create Entry ------------------------------");
//    ClientEntity ce = loadEntity("/mymanufacturer.json");
//    entry = createEntity(edm, serviceUrl, "Manufacturers", ce);

//    print("\n----- Update Entry ------------------------------");
//    ce = loadEntity("/mymanufacturer2.json");
//    int sc = updateEntity(edm, serviceUrl, "Manufacturers", 123, ce);
//    print("Updated successfully: " + sc);
//    entry = readEntityWithKey(edm, serviceUrl, "Manufacturers", 123);
//    print("Updated Entry successfully: " + prettyPrint(entry.getProperties(), 0));
//
//    //
//    print("\n----- Delete Entry ------------------------------");
//    sc = deleteEntity(serviceUrl, "Manufacturers", 123);
//    print("Deletion of Entry was successfully: " + sc);
//
//    try {
//      print("\n----- Verify Delete Entry ------------------------------");
//      readEntityWithKey(edm, serviceUrl, "Manufacturers", 123);
//    } catch(Exception e) {
//      print(e.getMessage());
//    }
  }
}
