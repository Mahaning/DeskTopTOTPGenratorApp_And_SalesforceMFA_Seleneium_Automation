package TOTP;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SalesforceLoginTest {

    public static void main(String[] args) {

        String username = "<Username>"; // Replace with actual username
        String password = "<Password>"; // Replace with actual password
        String totpSecret = TotpSecretStore.getSecret(username);
        if (totpSecret == null) {
            throw new RuntimeException("TOTP secret not found for user: " + username);
        }


        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();

        driver.get("https://login.salesforce.com");

        // Username
        driver.findElement(By.id("username")).sendKeys(username);

        // Password
        driver.findElement(By.id("password")).sendKeys(password);

        driver.findElement(By.id("Login")).click();

         //Wait for MFA input
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text']")
        ));

        // Generate OTP
        String otp;
		
			TOTPGenerator totpGenerator = new TOTPGenerator();
			 otp = totpGenerator.generateTotp(totpSecret);
		

        // Enter OTP
        driver.findElement(By.xpath("//input[@type='text']")).sendKeys(otp);

        driver.findElement(By.xpath("//input[@value='Verify']")).click();

        // Wait for home page
        wait.until(ExpectedConditions.titleContains("Home"));

        System.out.println("Login successful with MFA");
    }
}
