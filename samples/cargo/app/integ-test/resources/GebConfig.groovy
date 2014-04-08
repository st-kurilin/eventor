import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.htmlunit.HtmlUnitDriver

driver = {
    def driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17)
    driver
}
baseUrl = 'http://localhost:8080/'
reportsDir = 'samples/cargo/app/build/reports/integ-test'
waiting {
    timeout = 5 // seconds.
}
