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
TARGET_DNS = 'REDACTED'
GLOBAL_WAIT = 15


def login_cloudflare(browser):
    browser.get('https://dash.cloudflare.com/login')
    browser.find_element_by_name('email').send_keys(USERNAME)
    browser.find_element_by_name('password').send_keys(PASSWORD)
    last = (browser.current_url,'https://dash.cloudflare.com/')
    browser.find_element_by_xpath("//button[@type='submit']").click()
    while browser.current_url in last : pass
    return browser.current_url

def select_domain(browser,base,dom):
    base+='/'+dom
    browser.get(base)
    return base

def add_domain_record(browser,base,name,target): #CNAME
    browser.get(base+'/dns')
    WebDriverWait(browser, GLOBAL_WAIT).until(EC.presence_of_element_located((By.XPATH, "//span[text() = 'Add record']")))
    browser.find_element_by_xpath("//span[text() = 'Add record']").click() 
    WebDriverWait(browser, GLOBAL_WAIT).until(EC.presence_of_element_located((By.XPATH, '//form')))
    browser.find_element_by_tag_name('form').find_elements_by_tag_name('div')[1].find_elements_by_tag_name('div')[0].find_elements_by_tag_name('div')[0].click()
    browser.find_elements_by_xpath("//div[@id='react-select-2-option-4']")[0].click()
    browser.find_element_by_xpath("//textarea[@name='name']").send_keys(name)
    browser.find_element_by_xpath("//textarea[@name='target']").send_keys(target)
    browser.find_element_by_xpath("//button[@type='submit']").click()
    WebDriverWait(browser, GLOBAL_WAIT).until_not(EC.presence_of_element_located((By.XPATH, '//form')))

def ssl_settings(browser,base):
    browser.get(base + '/ssl-tls')
    WebDriverWait(browser, GLOBAL_WAIT).until(EC.presence_of_element_located((By.XPATH, "//label[@for='flexible']")))
    browser.find_element_by_xpath("//label[@for='flexible']").find_element_by_xpath('..').click()
    browser.find_element_by_xpath("//label[@for='flexible']").find_element_by_xpath('..').click()
    sleep(1)
    #browser.get(base + '/ssl-tls/edge-certificates')
    #sleep(10)
    #browser.find_element_by_xpath("//article[@id='always_use_https']").find_element_by_xpath("//label").click()

def set_up_domain(domain_name):
    
    browser_creation = False
    try:
        opt = Options()
        opt.headless = True
        browser = Firefox(options=opt)
        browser_creation = True
    except:pass
    if browser_creation:
        try:
            base_url = login_cloudflare(browser)
            base_url = select_domain(browser,base_url,domain_name)
            add_domain_record(browser,base_url,'@',TARGET_DNS)
            ssl_settings(browser,base_url)
            browser.close()
            browser.quit()
            return 'ok'
        except Exception as e:
            browser.close()
            browser.quit()
            return 'unexpected_exception'
    else:
        return 'browser_creation_fail'

