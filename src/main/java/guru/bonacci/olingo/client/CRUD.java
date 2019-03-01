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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ContentType;


public class CRUD {
  private ODataClient client;
  
  public CRUD(ODataClient client) {
    this.client = client;
  }

  public Edm readEdm(String serviceUrl) throws IOException {
    EdmMetadataRequest request = client.getRetrieveRequestFactory().getMetadataRequest(serviceUrl);
    ODataRetrieveResponse<Edm> response = request.execute();
    return response.getBody();
  }

  public ClientEntitySetIterator<ClientEntitySet, ClientEntity> readEntities(Edm edm, String serviceUri,
    String entitySetName) {
    URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName).build();
    return readEntities(edm, absoluteUri);
  }

  public ClientEntitySetIterator<ClientEntitySet, ClientEntity> readEntitiesWithFilter(Edm edm, String serviceUri,
    String entitySetName, String filterName) {
    URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName).filter(filterName).build();
    return readEntities(edm, absoluteUri);
  }

  private ClientEntitySetIterator<ClientEntitySet, ClientEntity> readEntities(Edm edm, URI absoluteUri) {
    System.out.println("URI = " + absoluteUri);
    ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> request = 
      client.getRetrieveRequestFactory().getEntitySetIteratorRequest(absoluteUri);
    // odata4 sample/server limitation not handling metadata=full
    request.setAccept("application/json;odata.metadata=minimal");
    ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> response = request.execute(); 
      
    return response.getBody();
  }

  public ClientEntity readEntityWithKey(Edm edm, String serviceUri, String entitySetName, Object keyValue) {
    URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName)
      .appendKeySegment(keyValue).build();
    return readEntity(edm, absoluteUri);
  }

  public ClientEntity readEntityWithKeyExpand(Edm edm, String serviceUri, String entitySetName, Object keyValue,
    String expandRelationName) {
    URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName).appendKeySegment(keyValue)
      .expand(expandRelationName).build();
    return readEntity(edm, absoluteUri);
  }

  private ClientEntity readEntity(Edm edm, URI absoluteUri) {
    ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory().getEntityRequest(absoluteUri);
    // odata4 sample/server limitation not handling metadata=full
    request.setAccept("application/json;odata.metadata=minimal");
    ODataRetrieveResponse<ClientEntity> response = request.execute(); 
      
    return response.getBody();
  }
  
  private ClientEntity loadEntity(String path) throws ODataDeserializerException {
    InputStream input = getClass().getResourceAsStream(path);
    return client.getBinder().getODataEntity(client.getDeserializer(ContentType.APPLICATION_JSON).toEntity(input));
  }

  public ClientEntity createEntity(Edm edm, String serviceUri, String entitySetName, ClientEntity ce) {
    URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName).build();
    return createEntity(edm, absoluteUri, ce);
  }

  private ClientEntity createEntity(Edm edm, URI absoluteUri, ClientEntity ce) {
    ODataEntityCreateRequest<ClientEntity> request = client.getCUDRequestFactory()
      .getEntityCreateRequest(absoluteUri, ce);
    // odata4 sample/server limitation not handling metadata=full
    request.setAccept("application/json;odata.metadata=minimal");
    ODataEntityCreateResponse<ClientEntity> response = request.execute(); 
      
    return response.getBody();
  }

  public int updateEntity(Edm edm, String serviceUri, String entityName, Object keyValue, ClientEntity ce) {
    URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entityName)
      .appendKeySegment(keyValue).build();
    ODataEntityUpdateRequest<ClientEntity> request = 
      client.getCUDRequestFactory().getEntityUpdateRequest(absoluteUri, UpdateType.PATCH, ce);
    // odata4 sample/server limitation not handling metadata=full
    request.setAccept("application/json;odata.metadata=minimal");
    ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    return response.getStatusCode();
  }

  public int deleteEntity(String serviceUri, String entityName, Object keyValue) throws IOException {
    URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entityName)
      .appendKeySegment(keyValue).build();
    ODataDeleteRequest request = client.getCUDRequestFactory().getDeleteRequest(absoluteUri);
    // odata4 sample/server limitation not handling metadata=full
    request.setAccept("application/json;odata.metadata=minimal");
    ODataDeleteResponse response = request.execute();
    return response.getStatusCode();
  }
}
