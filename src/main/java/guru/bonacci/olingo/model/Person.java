package guru.bonacci.olingo.model;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EdmEntity(namespace = "SDL.OData.Example", key = "id", containerName = "SDLExample")
@EdmEntitySet
public class Person {

    @EdmProperty(name = "id", nullable = false)
    private String personId;

    @EdmProperty(name = "firstName", nullable = false)
    private String firstName;

    @EdmProperty(name = "lastName", nullable = true)
    private String lastName;

    @EdmProperty(name = "age", nullable = false)
    private int age;
}
