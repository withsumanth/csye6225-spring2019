# CSYE 6225 - Spring 2019

## Team Information

|                Name            | NEU ID    |         Email Address              |
| ------------------------------ | --------- | ---------------------------------- |
|Sumanth Hagalavadi Gopalakrishna| 001824723 | hagalavadigopalakr.s@husky.neu.edu |
|Prashant Kabra                  | 001238302 | kabra.p@husky.neu.edu              |
|Ashish harishchandra Gurav      | 001837129 | gurav.a@husky.neu.edu              |
|Nivetha Ganeshjeevan	         | 001206641 | ganeshjeevan.n@husky.neu.edu       |

## Technology Stack

Programming Language and framework used: Java, Spring Boot Framework, MySQL, Maven


Prerequisites for building and deploying your application locally
1. Eclipse IDE with spring suite
2. Postman
3. MySQL

## Build and Deploy Instructions

1. Create a spring boot application
2. Update application.properties file with database connection details
3. Run the application as "Java Application"
4. Open Postman application to test API results
5. Now select the POST option and enter the URL as "http://localhost:8080/user/register"
6. In the body section below, select 'raw' and then select 'JSON(application/json)'
7. Write the parameters to be sent in JSON format and click on 'Send', see the results on the window below
6. Now select GET option and enter the URL as "http://localhost:8080/"
7. In the 'authorization' section, select 'Basic Auth'
8. Enter the credentials and click 'Send'
9. If the credentials are correct, the current timestamp is shown

## REST API endpoints
- Register a new user
  * **Method:** `POST`
  * **URL:** `/user/register`
  * **Success Response:**

  * **Code:** `201 CREATED`
 
  * **Error Response:**

  * **Code:** `400 BAD REQUEST`
    **Content:** 
    ```json
    {"error" : "BAD REQUEST" }
    ```

    OR

  * **Code:** `409 CONFLICT` 
    **Content:** 
    ```json
    { "error" : "User already exits" }
    ```
All below endpoints are authenticated with basic authentication
- Get current time
  * **Method:** `GET`
`URL:**` /`
  * **Success Response:**

  * **Code:** `200 OK`
 
  * **Error Response:**

  * **Code:** `401 UNAUTHORIZED`
    **Content:** 
    ```json
    {"error" : "You are unauthorized to make this request." }
    ```

     OR

  * **Code:** `409 CONFLICT`
    **Content:** 
    ```json
    { "error" : "User already exits" }
    ```
- Get all notes 
  * **Method:**` GET    `
  * **URL:**`/note`
  * **Success Response:**

  * **Code:** `200 OK`
    **Content:** 
    ```json
    [
      {
        "id": "d290f1ee-6c54-4b01-90e6-d701748f3421",
        "content": "Content of the note",
        "title": "Title of the note",
        "created_on": "2016-08-29T09:12:33.001Z",
        "last_updated_on": "2016-08-29T09:12:33.001Z"
        }
    ]
    ```
 
  * **Error Response:**

  * **Code:** `401 UNAUTHORIZED`
    **Content:** 
    ```json
    { "error" : "You are unauthorized to make this request." }
    ```

- Get note by id
  * **Method:**` GET`
  * **URL:**` /note/:id`
  * **Success Response:**

  * **Code:** `200 OK`
    **Content:** 
    ```json
      {
        "id": "d290f1ee-6c54-4b01-90e6-d701748f3421",
        "content": "Content of the note",
        "title": "Title of the note",
        "created_on": "2016-08-29T09:12:33.001Z",
        "last_updated_on": "2016-08-29T09:12:33.001Z"
        }
    ```
 
  * **Error Response:**

  * **Code:** `404 NOT FOUND`
    **Content:** 
    ```json
    {"error" : "User doesn't exist" }
    ```

     OR

  * **Code:**`401 UNAUTHORIZED`
    **Content:** 
    ```json
    { "error" : "You are unauthorized to make this request." }
    ```
- Create a new note
  * **Method:**` POST`
  * **URL:**` /note/`
  * **Success Response:**

  * **Code:** `201 CREATED`
    **Content:** 
    ```json
      {
        "id": "d290f1ee-6c54-4b01-90e6-d701748f3421",
        "content": "Content of the note",
        "title": "Title of the note",
        "created_on": "2016-08-29T09:12:33.001Z",
        "last_updated_on": "2016-08-29T09:12:33.001Z"
        }
    ```
 
  * **Error Response:**

  * **Code:** `400 BAD REQUEST`
    **Content:** 
    ```json
    {"error" : "BAD REQUEST" }
    ```

     OR

  * **Code:** `401 UNAUTHORIZED`
    **Content:** 
    ```json
    { "error" : "You are unauthorized to make this request." }
    ```
- Update note by id
  * **Method:**` PUT`
  * **URL:**` /note/:id`
  * **Success Response:**

  * **Code:** `204 NO CONTENT`
 
  * **Error Response:**

  * **Code:** `400 BAD REQUEST`
    **Content:** 
    ```json
    {"error" : "BAD REQUEST" }
    ```

     OR

  * **Code:** `401 UNAUTHORIZED`
    **Content:** 
    ```json
    { "error" : "You are unauthorized to make this request." }
    ```
- Delete note by id
  * **Method:**`DELETE`
  * **URL:** /note/:id`
  * **Success Response:**

  * **Code:** `204 NO CONTENT`
 
  * **Error Response:**

  * **Code:** `400 BAD REQUEST`
    **Content:** 
    ```json
    {"error" : "BAD REQUEST" }
    ```

    OR

  * **Code:** `401 UNAUTHORIZED`
    **Content:** 
    ```json
    { "error" : "You are unauthorized to make this request." }

- Attach file to note identified by 'id'
  * **Method** `POST`
  * **URL:** /note/{idNotes}/attachments
  * **Success Response:**
  * **Code:** `200 OK`

  * **Error Response:**
  
  **Code** `401 Unauthorized`

- Get list of files attached to the note identified by the ‘id’
  * **Method** `GET`
  * **URL:** /note/{idNotes}/attachments
  * **Success Response:**
  * **Code:** `200 OK`

  * **Error Response:**
  
  **Code** `401 Unauthorized`

- Update file identified by ‘idAttachments’ attached to the note identified by the ‘id’
  * **Method** `PUT`
  * **URL:** /note/{idNotes}/attachments/{idAttachments}
  * **Success Response:**
  * **Code:** `204 No Content`

  * **Error Response:**
  
  **Code** `401 Unauthorized`

- Delete file identified by ‘idAttachments’ attached to the transaction identified by the ‘id’
  * **Method** `DELETE`
  * **URL:** /note/{idNotes}/attachments/{idAttachments}
  * **Success Response:**
  * **Code:** `204 No Content`

  * **Error Response:**
  
  **Code** `401 Unauthorized`

    ```












