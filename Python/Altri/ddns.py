from __future__ import print_function

APITOKEN = None
MAIL = None
DNSURL = None
TIME_WAIT = None

import os
import sys
import re
import json
import requests
import time

sys.path.insert(0, os.path.abspath('..'))
import CloudFlare

def my_ip_address():
    """Cloudflare API code - example"""

    # This list is adjustable - plus some v6 enabled services are needed
    # url = 'http://myip.dnsomatic.com'
    # url = 'http://www.trackip.net/ip'
    # url = 'http://myexternalip.com/raw'
    url = 'https://api.ipify.org'
    try:
        ip_address = requests.get(url).text
    except:
        exit('%s: failed' % (url))
    if ip_address == '':
        exit('%s: failed' % (url))

    if ':' in ip_address:
        ip_address_type = 'AAAA'
    else:
        ip_address_type = 'A'

    return ip_address, ip_address_type

def do_dns_update(cf, zone_name, zone_id, dns_name, ip_address, ip_address_type):
    """Cloudflare API code - example"""

    try:
        params = {'name':dns_name, 'match':'all', 'type':ip_address_type}
        dns_records = cf.zones.dns_records.get(zone_id, params=params)
    except CloudFlare.exceptions.CloudFlareAPIError as e:
        exit('/zones/dns_records %s - %d %s - api call failed' % (dns_name, e, e))

    updated = False

    # update the record - unless it's already correct
    for dns_record in dns_records:
        old_ip_address = dns_record['content']
        old_ip_address_type = dns_record['type']

        if ip_address_type not in ['A', 'AAAA']:
            # we only deal with A / AAAA records
            continue

        if ip_address_type != old_ip_address_type:
            # only update the correct address type (A or AAAA)
            # we don't see this becuase of the search params above
            print('IGNORED: %s %s ; wrong address family' % (dns_name, old_ip_address))
            continue

        if ip_address == old_ip_address:
            print('UNCHANGED: %s %s' % (dns_name, ip_address))
            updated = True
            continue

        proxied_state = dns_record['proxied']
 
        # Yes, we need to update this record - we know it's the same address type

        dns_record_id = dns_record['id']
        dns_record = {
            'name':dns_name,
            'type':ip_address_type,
            'content':ip_address,
            'proxied':proxied_state
        }
        try:
            dns_record = cf.zones.dns_records.put(zone_id, dns_record_id, data=dns_record)
        except CloudFlare.exceptions.CloudFlareAPIError as e:
            exit('/zones.dns_records.put %s - %d %s - api call failed' % (dns_name, e, e))
        print('UPDATED: %s %s -> %s' % (dns_name, old_ip_address, ip_address))
        updated = True

    if updated:
        return

    # no exsiting dns record to update - so create dns record
    dns_record = {
        'name':dns_name,
        'type':ip_address_type,
        'content':ip_address
    }
    try:
        dns_record = cf.zones.dns_records.post(zone_id, data=dns_record)
    except CloudFlare.exceptions.CloudFlareAPIError as e:
        exit('/zones.dns_records.post %s - %d %s - api call failed' % (dns_name, e, e))
    print('CREATED: %s %s' % (dns_name, ip_address))

def main():
    """Cloudflare API code - example"""

    dns_name = DNSURL

    host_name, zone_name = '.'.join(dns_name.split('.')[:2]), '.'.join(dns_name.split('.')[-2:])

    ip_address, ip_address_type = my_ip_address()

    print('MY IP: %s %s' % (dns_name, ip_address))

    cf = CloudFlare.CloudFlare(token=APITOKEN,email=MAIL)

    # grab the zone identifier
    try:
        params = {'name':zone_name}
        zones = cf.zones.get(params=params)
    except CloudFlare.exceptions.CloudFlareAPIError as e:
        exit('/zones %d %s - api call failed' % (e, e))
    except Exception as e:
        exit('/zones.get - %s - api call failed' % (e))

    if len(zones) == 0:
        exit('/zones.get - %s - zone not found' % (zone_name))

    if len(zones) != 1:
        exit('/zones.get - %s - api call returned %d items' % (zone_name, len(zones)))

    zone = zones[0]

    zone_name = zone['name']
    zone_id = zone['id']

    do_dns_update(cf, zone_name, zone_id, dns_name, ip_address, ip_address_type)

if __name__ == '__main__':
    try:
        f = open('key.conf','r')
        MAIL = f.readline().replace(' ','').replace('\n','')
        APITOKEN = f.readline().replace(' ','').replace('\n','')
        DNSURL = f.readline().replace(' ','').replace('\n','')
        TIME_WAIT = int(f.readline().replace(' ','').replace('\n',''))
    except:
        print("key.conf expected: 1.E-MAIL, 2.API, 3.RECORDNAME, 4.SECOND_UPDATE_TIME")
        exit(1)

    while True:
        try:
            main()
            time.sleep(TIME_WAIT)
        except:
            try:
                time.sleep(10)
            except:
                pass