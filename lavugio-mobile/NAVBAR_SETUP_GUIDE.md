# Navbar Setup Guide for Developers

## Quick Start

### Step 1: Make Your Activity Extend BaseActivity
```java
public class MyActivity extends BaseActivity {
    // instead of extends AppCompatActivity
}
```

### Step 2: Initialize Navbar in onCreate()
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my);
    
    initializeNavbar();  // ADD THIS LINE
}
```

### Step 3: Handle Navigation Clicks
```java
@Override
protected void onNavbarItemClicked(String itemId) {
    super.onNavbarItemClicked(itemId);
    
    switch (itemId) {
        case "trips":
            navigateToTrips();
            break;
        case "history":
            navigateToHistory();
            break;
        // ... etc
    }
}
```

## Complete Example Activity

```java
package com.example.lavugio_mobile;

import android.os.Bundle;
import android.content.Intent;

public class MyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initializeNavbar();
    }

    @Override
    protected void onNavbarItemClicked(String itemId) {
        super.onNavbarItemClicked(itemId);
        
        switch (itemId) {
            case "trips":
                startActivity(new Intent(this, TripsActivity.class));
                break;
            case "history":
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case "reports":
                startActivity(new Intent(this, ReportsActivity.class));
                break;
            case "profile":
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case "login":
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case "register":
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }
}
```

## Adding Navbar to Existing Activity

1. Change class declaration:
   ```java
   // Before:
   public class ExistingActivity extends AppCompatActivity {
   
   // After:
   public class ExistingActivity extends BaseActivity {
   ```

2. Add initialization in onCreate():
   ```java
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_existing);
       
       initializeNavbar();  // Add this
       
       // ... rest of your code
   }
   ```

3. Override navigation handler:
   ```java
   @Override
   protected void onNavbarItemClicked(String itemId) {
       super.onNavbarItemClicked(itemId);
       // Add your navigation logic here
   }
   ```

## Navbar Item IDs

These are the available navbar item IDs you can handle:

| Item ID | Label | Purpose |
|---------|-------|---------|
| `"trips"` | Trips | Show user's trips |
| `"history"` | History | Show trip history |
| `"reports"` | Reports | Show reports/analytics |
| `"profile"` | Profile | Show user profile |
| `"login"` | Log In | Login screen |
| `"register"` | Register | Registration screen |

## Customizing Navbar Appearance

### Change Colors
Edit `res/values/colors.xml`:
```xml
<color name="lavugio_primary">#BC6C25</color>      <!-- Main navbar background -->
<color name="lavugio_secondary">#DDA15E</color>    <!-- Divider color -->
<color name="lavugio_light">#FEFAE0</color>        <!-- Text color -->
<color name="lavugio_dark">#283618</color>         <!-- Alternative dark color -->
```

### Change Menu Labels
Edit `res/values/strings.xml`:
```xml
<string name="nav_trips">Trips</string>
<string name="nav_history">History</string>
<string name="nav_reports">Reports</string>
<string name="nav_profile">Profile</string>
<string name="nav_login">Log In</string>
<string name="nav_register">Register</string>
```

### Customize Navbar Layout
Edit `res/layout/navbar.xml` to add/remove items or change structure.

## Using NavbarManager Directly

For more control, use NavbarManager in fragments or other components:

```java
NavbarManager navbarManager = new NavbarManager(getActivity(), itemId -> {
    switch (itemId) {
        case "trips":
            // handle
            break;
        // ...
    }
});

navbarManager.initializeNavbar();

// Later, when needed:
navbarManager.toggleMenu();
navbarManager.closeMenu();
```

## Common Issues

### Issue: Navbar not appearing
**Solution:** Make sure you call `initializeNavbar()` after `setContentView()`

### Issue: Navigation clicks not working
**Solution:** Ensure you override `onNavbarItemClicked()` and call `super.onNavbarItemClicked(itemId)`

### Issue: Menu stays open after navigation
**Solution:** The menu automatically closes - make sure `onNavbarItemClicked()` isn't preventing normal flow

### Issue: Layout inflation errors
**Solution:** Ensure `navbar.xml` is in `res/layout/` directory and IDs match

## File Locations

Important files to know:
- **Navbar Layout:** `res/layout/navbar.xml`
- **Colors:** `res/values/colors.xml`
- **Strings:** `res/values/strings.xml`
- **Base Class:** `java/com/example/lavugio_mobile/BaseActivity.java`
- **Utility Class:** `java/com/example/lavugio_mobile/NavbarManager.java`

## Testing the Navbar

1. Run the app
2. Verify navbar appears at top of all activities
3. Click hamburger icon - menu should expand
4. Click menu item - menu should close and navigate
5. Click back if menu open - menu should close
6. Check colors match design (brown #BC6C25)

## Support

For issues or questions:
1. Check NAVBAR_IMPLEMENTATION.md for detailed documentation
2. Review SampleActivity.java for working example
3. Check MainActivity.java for actual implementation example
