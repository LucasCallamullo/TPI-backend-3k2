<h1>Container Transport Logistics System – Backend (TPI 2025)</h1>

<p>This proposal corresponds to the Integrative Practical Work (TPI) of the Backend Applications course – Year 2025.</p>
<p>The overall objective of the project is to implement a microservices-based backend solution, focused on the comprehensive management of a land transport logistics system for containers used in housing construction.</p>
<p>In this scenario, the transport object is the container itself, not its content.</p>

[![Static Badge](https://img.shields.io/badge/Documentation-EN-blue)](https://github.com/LucasCallamullo/TPI-backend-3k2/blob/main/README.md) [![Documentation ES](https://img.shields.io/badge/Documentation-ES-green)](https://github.com/LucasCallamullo/TPI-backend-3k2/blob/main/README-ES.md)


### [EN]
<h3>Backend</h3>
<p>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white" alt="Spring Security">
</p>

<h3>Database & ORM</h3>
<p>
  <img src="https://img.shields.io/badge/H2_Database-0040CA?style=for-the-badge&logo=h2&logoColor=white" alt="H2 Database">
  <img src="https://img.shields.io/badge/JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="JPA Hibernate">
</p>

<h3>Security</h3>
<p>
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white" alt="JWT">
</p>

<h3>Tools & DevOps</h3>
<p>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white" alt="Postman">
  <img src="https://img.shields.io/badge/git%20-%23F05033.svg?&style=for-the-badge&logo=git&logoColor=white" alt="Git">
  <img src="https://img.shields.io/badge/github%20-%23121011.svg?&style=for-the-badge&logo=github&logoColor=white" alt="GitHub">
</p>

<h3>Testing & Docs</h3>
<p>
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger">
  <img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit">
</p>


<br>
<h2>👥 Contact Information</h2>
<a href="#contacto-section">Go to Contact ↓</a>


<br>
<h2>🐳 Docker Deployment</h2>

<p>All services are dockerized and can be started using Docker Compose, including:</p>
<ul> 
   <li>Microservices (Customers, Requests, Logistics)</li>
   <li>Gateway</li>
   <li>Keycloak</li>
   <li>Database</li>
   <li>OSRM (with preprocessed maps)</li>
   <li>Routing Service</li>
</ul>
<p>This allows for a unified, reproducible environment ready for testing or deployment.</p>


<br>
<h2>General Architecture</h2>

<p>The system follows a modern architecture composed of multiple independent microservices, each responsible for a specific domain:</p>

<h3>🔹 Customer Microservice</h3>
<p>Management of end users, integration with Keycloak for authentication and role management, creation and administration of registered customers in the system.</p>

<h3>🔹 Request Microservice</h3>
<p>Complete management of the transport request lifecycle: creation, container assignment, statuses, validations, and process tracking.</p>

<h3>🔹 Logistics Microservice</h3>
<p>Management of routes, segments, logistic statuses, and estimated cost calculation based on distances, consumption, rates, and assigned vehicle type.</p>

<h3>🔹 API Gateway</h3>
<p>Single entry point for all frontend applications or external clients. Responsible for routing and communication to each microservice, in addition to validating JWT tokens issued by Keycloak.</p>

<h3>🔹 Authentication Service – Keycloak</h3>
<p>The system uses Keycloak as identity and authentication provider:</p>
<ul> 
   <li>User management</li>
   <li>Roles and permissions</li>
   <li>JWT token issuance</li>
   <li>Administrative integration for automatic user creation</li>
</ul>

<h3>🔹 Routing Service (OSRM)</h3>
<p>Microservice dedicated exclusively to calculating routes and distances based on geographic coordinates (latitude/longitude). It consumes a Docker instance of OSRM (Open Source Routing Machine) and supports:</p>
<ul> 
   <li>Main route</li>
   <li>Alternative routes</li>
   <li>Distance calculation and estimated times</li>
   <li>This allows optimization of system costs and logistics.</li>
</ul>



<h3>C4 Model</h3>
<img src="https://raw.githubusercontent.com/LucasCallamullo/TPI-backend-3k2/refs/heads/main/docs/img/c4_model.png" alt="C4 Model Diagram">

<h3>Entity Relationship Diagrams - ERD</h3>
<img src="https://raw.githubusercontent.com/LucasCallamullo/TPI-backend-3k2/refs/heads/main/docs/img/der.png" alt="Entity Relationship Diagram">

<h3>Authentication Flow:</h3>
<img src="https://raw.githubusercontent.com/LucasCallamullo/TPI-backend-3k2/refs/heads/main/docs/img/auth_flow.png" alt="Authentication Flow Diagram">


<h4>Detailed Flow Steps</h4>
<ol>
  <li>
     <strong>Authentication Start:</strong> 
     <p>The user accesses the authentication URL in the browser:</p>
    <pre><code>http://localhost:8081/realms/tpi-backend/protocol/openid-connect/auth
  ?client_id=tpi-backend-client
  &response_type=code
  &redirect_uri=http://localhost:8080/api/login/oauth2/code/keycloak</code></pre>
  </li>
  
  <li>
     <strong>Login Interface:</strong> 
     <p>Keycloak presents the authentication form where the user enters their credentials (e.g.: client01 / 1234).
</p>
   </li>
  
  <li>
     <strong>Authorization Code Generation:</strong> 
     <p>After validating the credentials, Keycloak generates an authorization code and redirects to the Gateway:</p>
    <pre><code>http://localhost:8080/api/login/oauth2/code/keycloak?code=ABC123XYZ</code></pre>
  </li>
  
  <li>
     <strong>Exchange for JWT Token:</strong> 
     <p>The Gateway's AuthController receives the code and makes a POST request to Keycloak to exchange it for a JWT token.</p>
   </li>
  
  <li>
      <strong>Token Retrieval:</strong> 
      <p>Keycloak responds with the complete JWT containing:</p>
      <ul> 
         <li>User information.</li>
         <li>Assigned roles (ROLE_CLIENTE, ROLE_ADMIN).</li>
         <li>Expiration time.</li>
         <li>Security metadata.</li>
      </ul>
  </li>
</ol>


<br>

<h2 id="contacto-section">💻 Contact Lucas Callamullo - Back-End Developer</h2>

| [![GitHub](https://img.shields.io/badge/github-%23121011.svg?&style=for-the-badge&logo=github&logoColor=white)](https://github.com/LucasCallamullo) | [![LinkedIn](https://img.shields.io/badge/linkedin-%230077B5.svg?&style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/lucas-callamullo/) | [![Email](https://img.shields.io/badge/Email-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:lucascallamullo98@gmail.com) |
|:-:|:-:|:-:|

| [![Portfolio](https://img.shields.io/badge/Portfolio-%23000000.svg?style=for-the-badge&logo=react&logoColor=white)](https://github.com/LucasCallamullo) | [![Youtube Badge](https://img.shields.io/badge/YouTube%20-%23FF0000.svg?&style=for-the-badge&logo=YouTube&logoColor=white)](https://www.youtube.com/@lucas_clases_python) |
|:-:|:-:|
