# Bearer Token Authentication Implementation

## Overview
The application has been migrated to use stateless Bearer token authentication with Spring Security's native OAuth2 Resource Server and JWT (JSON Web Token).

## Key Changes

### 1. **Stateless Session Management**
- Session policy changed from `IF_REQUIRED` to `STATELESS`
- API requests are authenticated via Bearer tokens without server-side sessions
- Each request must include the `Authorization: Bearer <token>` header

### 2. **OAuth2 Resource Server**
- Leverages Spring Security's built-in `oauth2ResourceServer` configuration
- Native JWT decoder with HMAC-SHA256 signing
- No custom authentication filter required in the filter chain

### 3. **JWT Token Provider**
- Token generation using the `JwtTokenProvider` component
- Tokens contain check_number and roles claims
- Configurable expiration time (default: 24 hours)

## Authentication Flow

### 1. **Login Request**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "check_number": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "check_number": "user@example.com"
}
```

### 2. **Authenticated API Request**
```bash
GET /graphql
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "query": "{ assets { id name } }"
}
```

## Configuration

### JWT Settings (application.yml)
```yaml
security:
  jwt:
    secret: aZt2026-strong-secret-key-ict-assets-jwt-token
    expiration: 86400000  # 24 hours in milliseconds
```

### Security Endpoints
- **Public Endpoints:**
  - `POST /api/auth/login` - User login
  - `POST /api/auth/signup` - User registration
  - `GET /login` - Login page
  - `GET /signup` - Signup page
  - Static resources: `/css/**`, `/js/**`

- **Protected Endpoints:**
  - `GET /assets` - Asset list view (requires authentication)
  - `/graphql` - GraphQL API (requires Bearer token)
  - `/api/**` - All API endpoints (require Bearer token)

## Role-Based Access Control

- **USER:** Read-only access to assets
- **MANAGER:** Can create and update assets
- **ADMIN:** Full access including asset deletion

## Testing with Bearer Token

### Using cURL
```bash
# Get token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"check_number":"admin","password":"admin"}' | jq -r '.token')

# Make authenticated request
curl -X GET http://localhost:8080/graphql \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query":"{ assets { id name } }"}'
```

### Using Postman
1. Send POST request to `http://localhost:8080/api/auth/login`
2. Extract the `token` from the response
3. Add header `Authorization: Bearer <token>` to API requests
4. Call GraphQL endpoint with the Bearer token

## Security Features

✅ **Stateless Authentication:** No server-side session storage  
✅ **JWT-based:** Self-contained tokens with embedded claims  
✅ **HMAC-SHA256 Signing:** Secure token verification  
✅ **Role-based Access Control:** Method-level security with @PreAuthorize  
✅ **CSRF Protection:** Disabled for API (sessions are stateless)  
✅ **Password Encryption:** BCrypt hashing for all passwords  

## Token Expiration & Refresh

Currently, tokens expire after 24 hours. To implement token refresh:
1. Create a refresh token endpoint
2. Issue refresh tokens with longer expiration
3. Allow clients to obtain new access tokens using refresh tokens

## Troubleshooting

### Token Validation Fails
- Verify the `Authorization` header uses format: `Bearer <token>`
- Check token expiration time
- Ensure JWT secret in config matches token signing key

### 401 Unauthorized
- Verify Bearer token is included in request header
- Check token hasn't expired
- Validate user has appropriate roles for the endpoint

### 403 Forbidden
- User is authenticated but doesn't have required role
- Check @PreAuthorize annotations on controller methods
