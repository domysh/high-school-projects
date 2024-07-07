#!/usr/bin/python3
from cf_utils import CloudAdmin, get_cf_config

if __name__ == '__main__':
    try:
        CloudAdmin().ddns(get_cf_config()['target_dns'])
        print('DDNS status: Ok')
    except Exception as e:
        exit('DDNS status: Error,\nExiting...')
