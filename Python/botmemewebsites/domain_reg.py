from selenium.webdriver import Firefox
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from time import sleep

#Sorry non usavo ancora gli ENV, skill issue fortissimo :(
USERNAME = 'REDACTED'
PASSWORD = 'REDACTED'
GLOBAL_WAIT = 15
NS1 = 'REDACTED'
NS2 = 'REDACTED'

def get_login(browser):
    browser.get('https://my.freenom.com/clientarea.php')
    user_form = browser.find_element_by_name("username")
    user_form.send_keys(USERNAME)
    pass_form = browser.find_element_by_name("password")
    pass_form.send_keys(PASSWORD)
    browser.find_element_by_xpath("//input[@type='submit' and @value='Login']").click()

def find_domain_up(browser,domain):
    browser.get('https://my.freenom.com/domains.php')
    domain_form = browser.find_element_by_name("domainname")
    domain_form.send_keys(domain)
    browser.find_element_by_id("submitBtn").click()
    WebDriverWait(browser, GLOBAL_WAIT).until(EC.presence_of_element_located((By.XPATH, "//div[@class='allResults active']")))
    sleep(2)
    return 'none' not in browser.find_element_by_class_name("succes").get_attribute("style")

def check_price(browser):
    res = browser.find_element_by_id("top_domain")
    res = res.find_element_by_id("dprice_int")
    res = res.text.replace('\n','').replace(' ','')
    res = float(res)
    return res

def take_domain(browser,dom, dns1 = NS1 , dns2 = NS2 ):
    browser.get('https://my.freenom.com/cart.php?a=confdomains')
    browser.find_element_by_class_name("useDNS").click()
    browser.find_element_by_class_name("useOwnDNS").click()
    browser.find_element_by_id(dom+'_dn1').send_keys(dns1)
    browser.find_element_by_id(dom+'_dn2').send_keys(dns2)
    time = Select(browser.find_element_by_name(dom.replace('.','_')+"_period"))
    time.select_by_index(11) # 12 Month
    browser.find_element_by_id('configure_submit_button').click()
    WebDriverWait(browser, GLOBAL_WAIT).until(EC.presence_of_element_located((By.CLASS_NAME, 'customerDetails')))
    browser.find_element_by_class_name("customerDetails").find_element_by_tag_name('label').click()
    sleep(10)
    last = browser.current_url
    browser.find_element_by_id('formSubmit').click()
    while last == browser.current_url:pass

def register_domain(domain_name):
    
    browser_creation = False

    try:
        opt = Options()
        opt.headless = True
        browser = Firefox(options=opt)
        browser_creation = True
    except:pass
    if browser_creation:
        try:
            get_login(browser)
            res = 'unsetted_error'
            if find_domain_up(browser,domain_name):
                if check_price(browser) == 0:
                    take_domain(browser,domain_name)
                    res = 'ok'
                else:
                    res = 'not_free'
            else:
                res = 'not_available'
            browser.close()
            browser.quit()
            return res
        except Exception as e:
            browser.close()
            browser.quit()
            return 'unexpected_exception'
    else:
        return 'browser_creation_fail'

