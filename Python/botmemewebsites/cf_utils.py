import requests, CloudFlare, json

def my_ip_address():
    url = 'https://api.ipify.org'
    try:
        ip_address = requests.get(url).text
    except:
        return False
    if ip_address == '':
        return False

    if ':' in ip_address:
        ip_address_type = 'AAAA'
    else:
        ip_address_type = 'A'

    return ip_address, ip_address_type

def cf_getZone(dns_name: str):
    return '.'.join(dns_name.split('.')[-2:])  # second level dns ('example.com')

def cf_get_on_off(v: bool):
    if v:
        return 'on'
    else:
        return 'off'

def get_cf_config(conf_filename:str = 'cloud_admin.conf'):
    load_conf = False
    try:
        with open(conf_filename,'rt') as conf_file:
            conf = dict(json.loads(conf_file.read()))

            conf_keys = conf.keys()
            if 'email' in conf_keys and 'api_token' in conf_keys:
                if conf['email'] != '' and conf['api_token'] != '':
                    load_conf = True
    except: pass
    if load_conf:
        return conf
    else:
        basic_conf = {'email':'user@example.com','api_token':'your_cloudflare_api_token'}
        open(conf_filename,'wt').write(json.dumps(basic_conf,indent=4))
        exit('Key.conf generated, insert information needed')

class CloudAdmin:
    def do_dns_update(self, target: str, record: str, record_type: str, proxy: bool = True):
        zone_id = self.get_zone_id(target)
        params = {'name': target, 'match': 'all', 'type': record_type}
        dns_records = self.cf.zones.dns_records.get(zone_id, params=params)

        updated = False

        # update the record - unless it's already correct
        for dns_record in dns_records:
            old_ip_address = dns_record['content']
            old_ip_address_type = dns_record['type']

            if record_type != old_ip_address_type:
                continue

            if record == old_ip_address:
                updated = True
                continue

            # Yes, we need to update this record - we know it's the same address type

            dns_record_id = dns_record['id']
            dns_record = {
                'name': target,
                'type': record_type,
                'content': record,
                'proxied': proxy
            }

            self.cf.zones.dns_records.put(zone_id, dns_record_id, data=dns_record)
            updated = True

        if updated:
            return

        # no exsiting dns record to update - so create dns record
        dns_record = {
            'name': target,
            'type': record_type,
            'content': record,
            'proxied': proxy
        }
        self.cf.zones.dns_records.post(zone_id, data=dns_record)


    def get_zone_id(self, dns_name: str):

        dns_name = cf_getZone(dns_name)

        zones = self.cf.zones.get(params={'name': dns_name})
        if len(zones) != 1:
            raise Exception(f'bad answer for {dns_name} ({len(zones)} found, expected 1)')
        return zones[0]['id']


    def ddns(self, dns_name: str):
        ip_address, ip_address_type = my_ip_address()
        self.do_dns_update(dns_name, ip_address, ip_address_type,False)


    def get_account_id(self):
        accounts = self.cf.accounts.get()
        if len(accounts) != 1:
            raise Exception(f'bad answer for account details ({len(accounts)} found, expected 1)')
        return accounts[0]['id']


    def subscribe_zone(self, zone_name: str):
        subscription = {
            "name": zone_name,
            "account": {"id": self.get_account_id()},
            "jump_start": False,
            "type": "full"
        }

        return self.cf.zones.post(data=subscription)


    def set_ssl_settings(self, dns_name: str, status: str = 'flexible'):
        try:
            return cf.zones.settings.ssl.patch(self.get_zone_id(dns_name), data={"value": status})  # off, flexible, full, strict
        except:
            return False


    def set_always_https(self, dns_name: str, status: bool = True):
        try:
            return self.cf.zones.settings.always_use_https.patch(self.get_zone_id(dns_name), data={"value": cf_get_on_off(status)})
        except:
            return False

    def __init__(self, email: str = '', token: str = ''):
        if email == '' or token == '':
            conf = get_cf_config()
            token = conf['api_token'] 
            email = conf['email']
        self.cf = CloudFlare.CloudFlare(token=token, email=email)
        self.cf.zones.get()
