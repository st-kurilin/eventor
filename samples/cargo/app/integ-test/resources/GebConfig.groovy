import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.htmlunit.HtmlUnitDriver

driver = {
    def driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17)
    driver
}
reportsDir = 'samples/cargo/app/build/reports/integ-test'
waiting {
    // seconds
    timeout = 5
}
