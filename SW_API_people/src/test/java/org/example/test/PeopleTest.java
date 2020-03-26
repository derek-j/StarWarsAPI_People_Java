package org.example.test;
import java.util.ArrayList;

import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

// Written by Derek Johnson
// 3/25/2020
// Assignment was to write 5 test cases to test the StarWars People REST API
// https://swapi.co/documentation#people
// These are the People Endpoints which are read-only:
//  - /people/ -- get all the people resources
//  - /people/:id/ -- get a specific people resource
//  - /people/schema/ -- view the JSON schema for this resource


public class PeopleTest {

    @Test
    // Verify count = 87 people in the return array
    // Verify entry 0 has dictionary name of "Luke Skywalker"
    // Verify the first 10 entries in the list
    // (Note this could change) and we would know if this test breaks!
    public void validateAllPeople() {
        given().get("https://swapi.co/api/people/")
                .then()
                .statusCode(200)
                .and()
                .body("count", is(87))
                .and()
                .body("results[0]", hasEntry("name", "Luke Skywalker"))
                .body("results[1]", hasEntry("name", "C-3PO"))
                .body("results[2]", hasEntry("name", "R2-D2"))
                .body("results[3]", hasEntry("name", "Darth Vader"))
                .body("results[4]", hasEntry("name", "Leia Organa"))
                .body("results[5]", hasEntry("name", "Owen Lars"))
                .body("results[6]", hasEntry("name", "Beru Whitesun lars"))
                .body("results[7]", hasEntry("name", "R5-D4"))
                .body("results[8]", hasEntry("name", "Biggs Darklighter"))
                .body("results[9]", hasEntry("name", "Obi-Wan Kenobi"));
    }


    @Test
    // Test accessing a single character in the people resource
    // Yoda is character 20.
    public void validatePeople_Yoda_20() {
        given().get("https://swapi.co/api/people/20")
                .then()
                .statusCode(200)
                .and()
                .body("name", hasToString("Yoda"))
                .and()
                .body("birth_year", hasToString("896BBY"));
    }


    @Test
    // Test accessing a single non-valid character ie 11111.
    // Should return a 404 and 'Not found' message.
    public void validatePeople_non_valid_person_11111() {
        given().get("https://swapi.co/api/people/11111")
                .then()
                .statusCode(404)
                .and()
                .body("detail", hasToString("Not found"));
    }


    @Test
    // Test accessing page2
    // Expected to have Next page point to page 3
    // Expected to have previous page point to page 1
    // Expected to have entry[0] be "Anakin Skywalker"
    // Expected to have entry [2] be "Chewbacca"
    public void validatePageSearch_page2() {
        given().get("https://swapi.co/api/people/?page=2")
                .then()
                .statusCode(200)
                .and()
                .body("next", hasToString("https://swapi.co/api/people/?page=3"))
                .and()
                .body("previous", hasToString("https://swapi.co/api/people/?page=1"))
                .and()
                .body("results[0]", hasEntry("name", "Anakin Skywalker"))
                .and()
                .body("results[2]", hasEntry("name", "Chewbacca"));
    }

        @Test
    // Test searching for a person
    // This returns a Next Query string to get that person.
    // ie in this case of Chewbacca, points to page 2 and Chewbacca
    // I don't think this API has been implemented/fleshed out yet.  as its not consistent with other search fields.
    public void validatePeopleNameSearch_Chewbacca() {
        given().get("https://swapi.co/api/people/?name=Chewbacca")
                .then()
                    .statusCode(200)
                .and()
                    .body("next", hasToString("https://swapi.co/api/people/?page=2&name=Chewbacca"));
    }


    @Test
    // Test the People Schema
    // These are basic tests that the schema returns as expected.
    // we could build a schema test framework and test each of the required parameters on the people objects.
    public void validateSchema() {
        given().get("https://swapi.co/api/people/schema")
                .then()
                    .statusCode(200)
                .and()
                    .body("description", hasToString("A person within the Star Wars universe"))
                .and()
                    .body("title", hasToString("People"))
                .and()
                    .body("$schema", hasToString("http://json-schema.org/draft-04/schema"));
    }


    // Create a PeopleDetails class for using marshalling/unmarshalling the json string to/from an Object
    public class PeopleDetails {
        private String name;
        private String height;
        private String mass;
        private String hair_color;
        private String skin_color;
        private String eye_color;
        private String birth_year;
        private String gender;
        private String homeworld;
        private ArrayList<String> films;
        private ArrayList<String> species;
        private ArrayList<String> vehicles;
        private ArrayList<String> starships;
        private String created;
        private String edited;
        private String url;

        // constructor
        PeopleDetails(String name, String height, String mass, String hair_color, String skin_color, String eye_color,
                      String birth_year, String gender, String homeworld, ArrayList<String> films, ArrayList<String> species,
                      ArrayList<String> vehicles, ArrayList<String> starships, String created, String edited, String url) {
            this.name = name;
            this.height = height;
            this.mass = mass;
            this.hair_color = hair_color;
            this.skin_color = skin_color;
            this.eye_color = eye_color;
            this.birth_year = birth_year;
            this.gender = gender;
            this.homeworld = homeworld;
            this.films = films;
            this.species = species;
            this.vehicles = vehicles;
            this.starships = starships;
            this.created = created;
            this.edited = edited;
            this.url = url;
        }
    }

    @Test
    // Check that the films are correct for person 3
    // Also check home planet is correct for person 3.
    // Previously I have validated fields through the use of 'hasToString' and 'hasEntry'.
    // In this testcase I'm demonstrating a couple more ways to validate fields.
    // 1st way:  Simply look for strings in the returned json string.
    // 2nd way:  I'm demonstrating marshalling and unmarshalling the json string into a People class object, so we can easily access any element we want.
    //           For Real work this will be the most effect way to extract fields and validate fields...
    public void validatePagePerson_3() {
        String body_str = get("Https://swapi.co/api/people/3").asString();

        // Verify R2-D2 is in movies 1-7
        // Here we are just accessing the json string looking for films.
        System.out.println("Response Body is: " + body_str);
        Assert.assertEquals(body_str.contains("films/1") /*Expected value*/, true, "Response body contains films/1");
        Assert.assertEquals(body_str.contains("films/2") /*Expected value*/, true, "Response body contains films/2");
        Assert.assertEquals(body_str.contains("films/3") /*Expected value*/, true, "Response body contains films/3");
        Assert.assertEquals(body_str.contains("films/4") /*Expected value*/, true, "Response body contains films/4");
        Assert.assertEquals(body_str.contains("films/5") /*Expected value*/, true, "Response body contains films/5");
        Assert.assertEquals(body_str.contains("films/6") /*Expected value*/, true, "Response body contains films/6");
        Assert.assertEquals(body_str.contains("films/7") /*Expected value*/, true, "Response body contains films/7");

        // umarshall/deserialize json string to PeopleDetails object.  We can now easily access each element of the PeopleObject.
        Gson gson = new Gson();
        PeopleDetails new_user = gson.fromJson(body_str, PeopleDetails.class);
        System.out.print(new_user);
        Assert.assertEquals(new_user.films.get(0), "https://swapi.co/api/films/2/", "Expected to find https://swapi.co/api/film/2/");
        Assert.assertEquals(new_user.homeworld, "https://swapi.co/api/planets/8/", "Expected to find planet/8");
    }

}
