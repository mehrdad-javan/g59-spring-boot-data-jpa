# How to Call APIs

---

## 1. Core Components of an API Call

To make a successful request, you need four key elements:

1.  **URL (Endpoint):** Where is the resource?
    - Example: `http://localhost:8080/api/v1/users`
2.  **HTTP Method (Verb):** What do you want to do?
    - `GET` (Fetch), `POST` (Create), `PUT` (Replace), `PATCH` (Modify), `DELETE` (Remove).
3.  **Headers:** Meta-information about the request.
    - Most common: `Content-Type: application/json` (tells the server you are sending JSON).
    - `Accept: application/json` (tells the server you want to receive JSON).
4.  **Body (Payload):** The data you are sending (used for `POST`, `PUT`, `PATCH`).

---

## 2. Tools for Calling APIs

### A. IntelliJ IDEA (.http files) - *Recommended for this project*
The file `api-tests.http` in the project root is a built-in tool in IntelliJ.
- **How to use:** Open the file and click the green "Play" button next to any request.
- **Benefit:** Fast, saved within the project, and requires no external tools.

### B. Postman / Insomnia
Graphic tools designed specifically for API development.
- **How to use:** Enter the URL, select the method, add a body (if needed), and click **Send**.
- **Benefit:** Great for organizing complex requests and testing different environments.

### C. Swagger UI - *Interactive Documentation*
Swagger UI is a web-based tool built into this project that automatically generates documentation for all endpoints.
- **URL:** `http://localhost:8080/swagger-ui.html`
- **How to use:** Open the URL in a browser, click on an endpoint, click **"Try it out"**, fill in any parameters or body, and click **"Execute"**.
- **Benefit:** Allows you to explore, understand, and test the API without writing any code or using external tools.

### D. Web Browser
Browsers are great for testing `GET` requests only.
- **How to use:** Type `http://localhost:8080/api/demo/` in the address bar.
- **Constraint:** You cannot easily perform `POST`, `PUT`, or `DELETE` from the address bar.

### E. cURL (Command Line)
A terminal-based tool available on most systems.
- **Example (GET):** `curl -X GET http://localhost:8080/api/demo/`
- **Example (POST):** `curl -X POST http://localhost:8080/api/demo/create -H "Content-Type: application/json" -d "{\"name\":\"John\"}"`

---

## 3. Practical Examples (Using DemoController)

### Fetching Data (GET)
- **URL:** `http://localhost:8080/api/demo/`
- **Action:** Retrieves a welcome message.
- **Expected Response:** `200 OK`

### Creating Data (POST)
- **URL:** `http://localhost:8080/api/demo/create`
- **Headers:** `Content-Type: application/json`
- **Body (JSON):**
  ```json
  {
    "name": "New Resource",
    "description": "This is a test"
  }
  ```
- **Expected Response:** `201 Created`

### Replacing Data (PUT)
- **URL:** `http://localhost:8080/api/demo/update/1` (Where 1 is the ID)
- **Body (JSON):** Provide the **full** object.
- **Expected Response:** `200 OK`

---

## 4. Understanding the Response

When you call an API, the server replies with:
- **Status Code:** (e.g., `200` = Success, `404` = Not Found, `500` = Server Error).
- **Body:** Usually JSON data containing the resource or an error message.
- **Headers:** Info like `Content-Type` or `Date`.

### Tip:
Always check the **Status Code** first. It's the quickest way to know if your call was successful!
