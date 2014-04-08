import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.htmlunit.HtmlUnitDriver

driver = {
    def driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17)
    driver
}
reportOnTestFailureOnly = true
reportsDir = 'target/geb-reports'
waiting {
    timeout = 5
}
