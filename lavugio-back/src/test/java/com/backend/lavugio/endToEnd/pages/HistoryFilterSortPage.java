package com.backend.lavugio.endToEnd.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Page Object for History Filtering and Sorting functionality (2.9.3)
 * Student 3 - E2E Tests
 */
public class HistoryFilterSortPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Date Range Picker Elements
    @FindBy(xpath = "//mat-form-field//mat-label[contains(text(),'Enter range')]")
    WebElement dateRangeLabel;

    @FindBy(xpath = "//mat-datepicker-toggle//button")
    WebElement datePickerToggle;

    @FindBy(xpath = "//input[@matStartDate]")
    WebElement startDateInput;

    @FindBy(xpath = "//input[@matEndDate]")
    WebElement endDateInput;

    // Sort Header Elements
    @FindBy(xpath = "//app-header//div[contains(@class,'hover:cursor-pointer')][.//span[text()='Start']]")
    WebElement startSortHeader;

    @FindBy(xpath = "//app-header//div[contains(@class,'hover:cursor-pointer')][.//span[text()='Pickup']]")
    WebElement pickupSortHeader;

    @FindBy(xpath = "//app-header//div[contains(@class,'hover:cursor-pointer')][.//span[text()='Destination']]")
    WebElement destinationSortHeader;

    // Table Elements
    @FindBy(xpath = "//app-table//app-row")
    List<WebElement> rideRows;

    @FindBy(xpath = "//div[contains(text(),'Nemate vožnji u istoriji')]")
    WebElement noRidesMessage;

    @FindBy(xpath = "//div[contains(text(),'Došli ste do kraja')]")
    WebElement endOfListMessage;

    // Loading spinner
    @FindBy(xpath = "//div[contains(@class,'animate-spin')]")
    WebElement loadingSpinner;

    public HistoryFilterSortPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(15));
    }

    /**
     * Wait for the history page to load
     */
    public void waitForPageToLoad() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOf(startSortHeader),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-date-filter"))
        ));
        waitForLoadingToComplete();
    }

    /**
     * Wait for any loading spinner to disappear
     */
    public void waitForLoadingToComplete() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'animate-spin')]")));
        } catch (TimeoutException e) {
            // Spinner may not be visible if load is instant
        }
        // Additional wait for data to settle
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Open the date picker calendar
     */
    public void openDatePicker() {
        wait.until(ExpectedConditions.elementToBeClickable(datePickerToggle)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("mat-calendar")));
    }

    /**
     * Select a date range using the date picker
     * Uses keyboard navigation which is more reliable than clicking calendar cells
     * @param startDate Start date in format "dd/MM/yyyy"
     * @param endDate End date in format "dd/MM/yyyy"
     */
    public void selectDateRange(String startDate, String endDate) {
        // Click on the date picker toggle to open calendar
        wait.until(ExpectedConditions.elementToBeClickable(datePickerToggle)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("mat-calendar")));

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Navigate to start date and select it
        navigateAndSelectDate(start);
        
        // Small wait for range selection mode
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        
        // Navigate to end date and select it
        navigateAndSelectDate(end);

        // Wait for calendar to close and data to reload
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.tagName("mat-calendar")));
        } catch (TimeoutException e) {
            // Calendar might have closed already
        }
        waitForLoadingToComplete();
    }

    /**
     * Navigate to the correct month/year and select the day
     */
    private void navigateAndSelectDate(LocalDate targetDate) {
        // First ensure we're in month view (not year view)
        ensureMonthView();
        
        // Navigate to the correct month
        navigateToMonth(targetDate.getMonthValue(), targetDate.getYear());
        
        // Click on the day - use button element inside the cell
        selectDay(targetDate.getDayOfMonth());
    }

    /**
     * Ensure we're viewing a month (not year selection)
     */
    private void ensureMonthView() {
        try {
            WebElement periodButton = driver.findElement(By.xpath("//button[contains(@class,'mat-calendar-period-button')]"));
            String text = periodButton.getText().trim();
            // If showing just a year (e.g., "2026"), we're in year view - need to select a month
            if (text.matches("\\d{4}")) {
                // Click current month to get to month view
                WebElement currentMonth = driver.findElement(By.xpath("//td[contains(@class,'mat-calendar-body-cell') and contains(@class,'mat-calendar-body-active')]//button"));
                currentMonth.click();
                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        } catch (NoSuchElementException e) {
            // Already in month view
        }
    }

    /**
     * Navigate to a specific month and year using arrow buttons
     */
    private void navigateToMonth(int targetMonth, int targetYear) {
        for (int i = 0; i < 36; i++) { // Max 3 years navigation
            WebElement periodButton = driver.findElement(By.xpath("//button[contains(@class,'mat-calendar-period-button')]"));
            String periodText = periodButton.getText().trim();
            
            if (isCorrectMonthYear(periodText, targetMonth, targetYear)) {
                return; // We're at the right month
            }

            // Parse current month/year from period text (e.g., "FEB 2026" or "February 2026")
            int[] currentMonthYear = parseMonthYear(periodText);
            if (currentMonthYear == null) {
                // Can't parse, try clicking period button to get to month view
                periodButton.click();
                try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                continue;
            }

            int currentMonth = currentMonthYear[0];
            int currentYear = currentMonthYear[1];
            
            // Calculate target as months from epoch for comparison
            int targetMonths = targetYear * 12 + targetMonth;
            int currentMonths = currentYear * 12 + currentMonth;

            WebElement navButton;
            if (targetMonths < currentMonths) {
                // Need to go backward
                navButton = driver.findElement(By.xpath("//button[contains(@class,'mat-calendar-previous-button')]"));
            } else {
                // Need to go forward
                navButton = driver.findElement(By.xpath("//button[contains(@class,'mat-calendar-next-button')]"));
            }
            navButton.click();
            try { Thread.sleep(150); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    /**
     * Parse month and year from period text like "FEB 2026" or "February 2026"
     */
    private int[] parseMonthYear(String text) {
        String[] shortMonths = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        String[] longMonths = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        
        String upper = text.toUpperCase();
        int month = -1;
        
        for (int i = 0; i < 12; i++) {
            if (upper.contains(longMonths[i]) || upper.contains(shortMonths[i])) {
                month = i + 1;
                break;
            }
        }
        
        if (month == -1) return null;
        
        // Extract year
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d{4}").matcher(text);
        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group());
            return new int[]{month, year};
        }
        return null;
    }

    /**
     * Select a specific day in the current month view
     */
    private void selectDay(int day) {
        // Try different selectors for day cells
        String[] xpaths = {
            // Modern Angular Material - button inside cell
            "//td[contains(@class,'mat-calendar-body-cell')]//button[normalize-space()='" + day + "']",
            // Alternative - span with day number
            "//td[contains(@class,'mat-calendar-body-cell')]//*[normalize-space()='" + day + "']",
            // Older Angular Material
            "//td[contains(@class,'mat-calendar-body-cell')]/span[normalize-space()='" + day + "']",
            // Direct cell with aria-label containing the day
            "//td[contains(@class,'mat-calendar-body-cell') and contains(@aria-label,'" + day + "')]"
        };
        
        for (String xpath : xpaths) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                for (WebElement el : elements) {
                    if (el.isDisplayed() && el.isEnabled()) {
                        // Verify this is the correct day (not from adjacent month)
                        String text = el.getText().trim();
                        if (text.equals(String.valueOf(day))) {
                            wait.until(ExpectedConditions.elementToBeClickable(el)).click();
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                // Try next xpath
            }
        }
        
        // Last resort - just click any cell with matching text
        WebElement cell = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//td[contains(@class,'mat-calendar-body-cell') and not(contains(@class,'mat-calendar-body-disabled'))]//button[contains(text(),'" + day + "')]")));
        cell.click();
    }

    private boolean isCorrectMonthYear(String periodText, int month, int year) {
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        String targetMonth = months[month - 1];
        String upper = periodText.toUpperCase();
        return upper.contains(targetMonth) && periodText.contains(String.valueOf(year));
    }

    /**
     * Click on Start column header to sort by start date
     */
    public void clickSortByStart() {
        wait.until(ExpectedConditions.elementToBeClickable(startSortHeader)).click();
        waitForLoadingToComplete();
    }

    /**
     * Click on Pickup column header to sort by departure
     */
    public void clickSortByPickup() {
        wait.until(ExpectedConditions.elementToBeClickable(pickupSortHeader)).click();
        waitForLoadingToComplete();
    }

    /**
     * Click on Destination column header to sort by destination
     */
    public void clickSortByDestination() {
        wait.until(ExpectedConditions.elementToBeClickable(destinationSortHeader)).click();
        waitForLoadingToComplete();
    }

    /**
     * Get the number of visible rides in the table
     */
    public int getRideCount() {
        try {
            List<WebElement> rows = driver.findElements(By.xpath("//app-table//app-row"));
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if "No rides" message is displayed
     */
    public boolean isNoRidesMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(noRidesMessage)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Get list of start dates from visible rides
     */
    public List<String> getStartDates() {
        List<String> dates = new ArrayList<>();
        List<WebElement> rows = driver.findElements(By.xpath("//app-table//app-row"));
        for (WebElement row : rows) {
            try {
                WebElement dateElement = row.findElement(By.xpath(".//div[1]//span[1]"));
                dates.add(dateElement.getText());
            } catch (NoSuchElementException e) {
                // Skip if element not found
            }
        }
        return dates;
    }

    /**
     * Get list of pickup addresses from visible rides
     */
    public List<String> getPickupAddresses() {
        List<String> addresses = new ArrayList<>();
        List<WebElement> rows = driver.findElements(By.xpath("//app-table//app-row"));
        for (WebElement row : rows) {
            try {
                // Pickup is typically the 3rd column
                WebElement addressElement = row.findElement(By.xpath(".//div[contains(@class,'w-1/3')][1]"));
                addresses.add(addressElement.getText());
            } catch (NoSuchElementException e) {
                // Skip if element not found
            }
        }
        return addresses;
    }

    /**
     * Get list of destination addresses from visible rides
     */
    public List<String> getDestinationAddresses() {
        List<String> addresses = new ArrayList<>();
        List<WebElement> rows = driver.findElements(By.xpath("//app-table//app-row"));
        for (WebElement row : rows) {
            try {
                // Destination is typically the 4th column
                WebElement addressElement = row.findElement(By.xpath(".//div[contains(@class,'w-1/3')][2]"));
                addresses.add(addressElement.getText());
            } catch (NoSuchElementException e) {
                // Skip if element not found
            }
        }
        return addresses;
    }

    /**
     * Check if sort indicator (arrow) is displayed for Start column
     */
    public boolean isSortIndicatorVisibleForStart() {
        try {
            WebElement sortIndicator = startSortHeader.findElement(By.tagName("svg"));
            return sortIndicator.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if sort indicator (arrow) is displayed for Pickup column
     */
    public boolean isSortIndicatorVisibleForPickup() {
        try {
            WebElement sortIndicator = pickupSortHeader.findElement(By.tagName("svg"));
            return sortIndicator.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if sort indicator (arrow) is displayed for Destination column
     */
    public boolean isSortIndicatorVisibleForDestination() {
        try {
            WebElement sortIndicator = destinationSortHeader.findElement(By.tagName("svg"));
            return sortIndicator.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Click on a specific ride row by index (0-based)
     */
    public void clickRideRow(int index) {
        List<WebElement> rows = driver.findElements(By.xpath("//app-table//app-row"));
        if (index < rows.size()) {
            wait.until(ExpectedConditions.elementToBeClickable(rows.get(index))).click();
        }
    }

    /**
     * Check if the dates are sorted in descending order (newest first)
     */
    public boolean areDatesSortedDescending() {
        List<String> dates = getStartDates();
        if (dates.size() < 2) return true;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (int i = 0; i < dates.size() - 1; i++) {
            try {
                LocalDate current = LocalDate.parse(dates.get(i), formatter);
                LocalDate next = LocalDate.parse(dates.get(i + 1), formatter);
                if (current.isBefore(next)) {
                    return false;
                }
            } catch (Exception e) {
                // If parsing fails, try alternative format
                continue;
            }
        }
        return true;
    }

    /**
     * Check if the dates are sorted in ascending order (oldest first)
     */
    public boolean areDatesSortedAscending() {
        List<String> dates = getStartDates();
        if (dates.size() < 2) return true;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (int i = 0; i < dates.size() - 1; i++) {
            try {
                LocalDate current = LocalDate.parse(dates.get(i), formatter);
                LocalDate next = LocalDate.parse(dates.get(i + 1), formatter);
                if (current.isAfter(next)) {
                    return false;
                }
            } catch (Exception e) {
                // If parsing fails, try alternative format
                continue;
            }
        }
        return true;
    }

    /**
     * Check if pickup addresses are sorted alphabetically ascending
     */
    public boolean arePickupAddressesSortedAscending() {
        List<String> addresses = getPickupAddresses();
        if (addresses.size() < 2) return true;

        for (int i = 0; i < addresses.size() - 1; i++) {
            if (addresses.get(i).compareToIgnoreCase(addresses.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if pickup addresses are sorted alphabetically descending
     */
    public boolean arePickupAddressesSortedDescending() {
        List<String> addresses = getPickupAddresses();
        if (addresses.size() < 2) return true;

        for (int i = 0; i < addresses.size() - 1; i++) {
            if (addresses.get(i).compareToIgnoreCase(addresses.get(i + 1)) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if destination addresses are sorted alphabetically ascending
     */
    public boolean areDestinationAddressesSortedAscending() {
        List<String> addresses = getDestinationAddresses();
        if (addresses.size() < 2) return true;

        for (int i = 0; i < addresses.size() - 1; i++) {
            if (addresses.get(i).compareToIgnoreCase(addresses.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if destination addresses are sorted alphabetically descending
     */
    public boolean areDestinationAddressesSortedDescending() {
        List<String> addresses = getDestinationAddresses();
        if (addresses.size() < 2) return true;

        for (int i = 0; i < addresses.size() - 1; i++) {
            if (addresses.get(i).compareToIgnoreCase(addresses.get(i + 1)) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if date picker is displayed
     */
    public boolean isDatePickerDisplayed() {
        try {
            return datePickerToggle.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if sorting headers are displayed
     */
    public boolean areSortingHeadersDisplayed() {
        try {
            return startSortHeader.isDisplayed() && 
                   pickupSortHeader.isDisplayed() && 
                   destinationSortHeader.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
