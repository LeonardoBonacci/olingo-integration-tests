package guru.bonacci.sdl.model;

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EdmComplex
public class Address {

    @EdmProperty(name = "lastName", nullable = true)
    private String streetName;

    @EdmProperty(name = "age")
    private Integer houseNumber;

}
