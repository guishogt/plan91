# Google OAuth Setup Guide

This guide explains how to enable "Sign in with Google" for Plan 91.

## Prerequisites

- Access to [Google Cloud Console](https://console.cloud.google.com/)
- Railway project with Plan 91 deployed

## Step 1: Create Google OAuth Credentials

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Navigate to **APIs & Services** > **Credentials**
4. Click **Create Credentials** > **OAuth client ID**
5. Select **Web application** as the application type
6. Configure the OAuth client:
   - **Name**: Plan 91 (or your preferred name)
   - **Authorized redirect URIs**: Add your production URL:
     ```
     https://plan91-app-production.up.railway.app/login/oauth2/code/google
     ```
   - For local development, also add:
     ```
     http://localhost:8080/login/oauth2/code/google
     ```
7. Click **Create**
8. Copy the **Client ID** and **Client Secret**

## Step 2: Re-enable OAuth2 Code

The OAuth2 code is currently disabled. To re-enable it:

### 2.1 Restore the OAuth2 dependency in `pom.xml`

Change:
```xml
<!-- OAuth2 Client: Google login (temporarily disabled for debugging)
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
-->
```

To:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

### 2.2 Restore OAuth2 configuration files

Rename the disabled files back:
```bash
mv src/main/java/com/ctoblue/plan91/infrastructure/security/OAuth2ClientConfig.java.disabled \
   src/main/java/com/ctoblue/plan91/infrastructure/security/OAuth2ClientConfig.java

mv src/main/java/com/ctoblue/plan91/infrastructure/security/OAuth2LoginSuccessHandler.java.disabled \
   src/main/java/com/ctoblue/plan91/infrastructure/security/OAuth2LoginSuccessHandler.java
```

### 2.3 Update `Plan91Application.java`

Add the OAuth2 exclusions back:
```java
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;

@SpringBootApplication(exclude = {
    OAuth2ClientAutoConfiguration.class,
    OAuth2ResourceServerAutoConfiguration.class
})
```

### 2.4 Update `SecurityConfig.java`

Re-add the OAuth2 handler:
```java
private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

@Value("${GOOGLE_CLIENT_ID:}")
private String googleClientId;

public SecurityConfig(UserDetailsService userDetailsService, RateLimitFilter rateLimitFilter,
                      CsrfCookieFilter csrfCookieFilter, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
    this.userDetailsService = userDetailsService;
    this.rateLimitFilter = rateLimitFilter;
    this.csrfCookieFilter = csrfCookieFilter;
    this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
}

private boolean isOAuth2Enabled() {
    return googleClientId != null && googleClientId.contains("googleusercontent.com");
}
```

And in `securityFilterChain()`, add:
```java
if (isOAuth2Enabled()) {
    http.oauth2Login(oauth2 -> oauth2
        .loginPage("/login")
        .successHandler(oAuth2LoginSuccessHandler)
    );
}
```

### 2.5 Update `AuthController.java`

Re-add the OAuth2 enabled check:
```java
@Value("${GOOGLE_CLIENT_ID:}")
private String googleClientId;

private boolean isOAuth2Enabled() {
    return googleClientId != null && googleClientId.contains("googleusercontent.com");
}

@GetMapping("/login")
public String loginPage(Model model) {
    model.addAttribute("oauth2Enabled", isOAuth2Enabled());
    return "pages/login";
}
```

## Step 3: Set Environment Variables in Railway

1. Go to your Railway project dashboard
2. Select the **plan91-app** service
3. Go to **Variables**
4. Add the following variables:
   - `GOOGLE_CLIENT_ID`: Your Google OAuth Client ID (ends with `.apps.googleusercontent.com`)
   - `GOOGLE_CLIENT_SECRET`: Your Google OAuth Client Secret

## Step 4: Deploy

Commit your changes and deploy:
```bash
git add -A
git commit -m "Re-enable Google OAuth"
git push origin master
```

Railway will automatically redeploy with the new code.

## How It Works

- The `OAuth2ClientConfig` class only activates when `GOOGLE_CLIENT_ID` contains `googleusercontent.com`
- This prevents startup failures when OAuth credentials aren't configured
- The login page conditionally shows the "Sign in with Google" button based on the `oauth2Enabled` flag
- New users signing in with Google automatically get a User and HabitPractitioner created

## Troubleshooting

### App crashes on startup after enabling OAuth2

1. Verify `GOOGLE_CLIENT_ID` is a valid Google OAuth client ID (contains `googleusercontent.com`)
2. Check that `GOOGLE_CLIENT_SECRET` is set correctly
3. Check Railway logs for specific error messages

### "Sign in with Google" button doesn't appear

1. Verify `GOOGLE_CLIENT_ID` environment variable is set in Railway
2. Check that the value contains `googleusercontent.com`
3. Redeploy the app after setting variables

### OAuth redirect fails

1. Verify the redirect URI in Google Cloud Console matches exactly:
   ```
   https://plan91-app-production.up.railway.app/login/oauth2/code/google
   ```
2. Ensure there are no trailing slashes or typos
