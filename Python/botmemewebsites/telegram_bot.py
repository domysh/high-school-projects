import telepot, subprocess, domain_reg, os, pwd, grp
import cloudflare_setup
from time import sleep
from telepot.loop import MessageLoop
from cf_utils import *

#Sorry non usavo ancora gli ENV, skill issue fortissimo :(
user_accepted = 1337
SITE_POINT_TO = 'REDACTED'
TYPE_RECORD_POINT = 'CNAME'
CLOUDFLARE_CONFG_FILE = 'managed_nginx'
SITE_DIR = '/var/www/'
FILE_OWNER_USER = pwd.getpwnam('admin').pw_uid
FILE_OWNER_GROUP = grp.getgrnam("root").gr_gid

def set_permission(path):
    os.chown(path, FILE_OWNER_USER, FILE_OWNER_GROUP)

class g:
    chat_status = 'base'
    data_status = {}

def send_request(name,oth = {}):
    oth.update({'request': name})
    g.data_status = oth
    g.chat_status = 'job'

def reset_status(response = {}):
    g.data_status = response
    g.chat_status = 'base'

def message_manager(msg):
    content_type, chat_type, chat_id = telepot.glance(msg)
    if user_accepted == chat_id:
        if content_type == 'text':
            sended_msg = msg['text']
            command = sended_msg.split(' ')[0]
            if command == '/cancel' and g.chat_status != 'job':
                bot.sendMessage(user_accepted, 'Operation Deleted! ❌')
                reset_status()
                return
        if g.chat_status == 'base':
            if content_type == 'text':
                sended_msg = msg['text']
                command = sended_msg.split(' ')[0]
                if command == '/publicip':
                    bot.sendMessage(user_accepted,'Getting Public IP...')
                    send_request('public_ip')
                elif command == '/setip':
                    bot.sendMessage(user_accepted, 'Setting Public IP...')
                    send_request('ddns_hit')
                elif command == '/ping':
                    bot.sendMessage(user_accepted,'PONG! 🏓')
                elif command == '/srvrestart':
                    paramether = sended_msg.split(' ')
                    bot.sendMessage(user_accepted, 'Sending Request...')
                    if len(paramether) == 1:
                        send_request('service_restart',{'name':False})
                    elif len(paramether) == 2:
                        send_request('service_restart',{'name':paramether[1]})
                    else:
                        bot.sendMessage(user_accepted,'Bad request 😴')
                elif command == '/reboot':
                    bot.sendMessage(user_accepted,'Sending restart request! ⚠⚠⚠')
                    send_request('restart')
                elif command == '/addsite':
                    g.chat_status = 'addsite'
                    g.data_status['progess_status'] = 0
                    bot.sendMessage(user_accepted,'Let\'s start! 😎\nSend me the domain name to register')
                else:
                    bot.sendMessage(user_accepted,'Send me a valid command ⌨️')
            else:
                bot.sendMessage(user_accepted,'Send me a command ⌨️')
        elif g.chat_status == 'addsite':
            if g.data_status['progess_status'] == 0:
                if content_type == 'text':
                    sended_msg = msg['text']
                    if ' ' not in sended_msg:
                        g.data_status['dns_to_register'] = clear_dns_name(sended_msg)
                        bot.sendMessage(user_accepted,'Now tell me if you want a:\n\n- /img_site,\n\n- /redirect_site,\n\n - /yt_video_site,\n\n- /blank_site')
                        g.data_status['progess_status']+=1
                    bot.sendMessage(user_accepted,'Send a valid name')
                else:
                    bot.sendMessage(user_accepted,'Please send me the domain name!')
            elif g.data_status['progess_status'] == 1:
                if content_type == 'text':
                    sended_msg = msg['text']
                    g.data_status['progess_status'] = sended_msg
                    if sended_msg == '/img_site':
                        bot.sendMessage(user_accepted,'Ok, now send me an Image 🖼️')
                    elif sended_msg == '/redirect_site':
                        bot.sendMessage(user_accepted,'Ok now send me the link of destination page 🖱️')
                    elif sended_msg == '/yt_video_site':
                        bot.sendMessage(user_accepted,'Ok now send me the link of the youtube video 🎬' )
                    elif sended_msg == '/blank_site':
                        bot.sendMessage(user_accepted,'Ok I\'ll start to create the page! 📜' )
                        send_request('addsite',g.data_status)
                    else:
                        g.data_status['progess_status'] = 1
                else:
                    bot.sendMessage(user_accepted, 'Choose Please')
                pass
            elif g.data_status['progess_status'] == '/yt_video_site':
                if content_type == 'text':
                    sended_msg = msg['text'].replace(' ','').replace('\n','')
                    yt_domain = clear_dns_name(sended_msg)
                    success = True
                    try:
                        if yt_domain == 'youtu.be':
                            g.data_status['yt_video_code'] = sended_msg.split('/')[-1][:11]
                        elif yt_domain == 'youtube.com':
                            g.data_status['yt_video_code'] = sended_msg.split('?v=')[-1][:11]
                        else:
                            success = False
                    except Exception as e:
                        success = False
                    if success:
                        bot.sendMessage(user_accepted, f'Taked Video code:{g.data_status["yt_video_code"]}\nStarting to create the site 🎬')
                        send_request('addsite',g.data_status)
                    else:
                        bot.sendMessage(user_accepted, 'Send a valid youtube link please')
                else:
                    bot.sendMessage(user_accepted, 'Send a link please')
            elif g.data_status['progess_status'] == '/redirect_site':
                if content_type == 'text':
                    sended_msg = msg['text'].replace(' ','').replace('\n','')
                    success = False
                    if sended_msg.startswith('http://') or sended_msg.startswith('https://'):
                        if '.' in sended_msg:
                            g.data_status['redirect_link'] = sended_msg
                            send_request('addsite',g.data_status)
                            success = True
                        if not success:
                            bot.sendMessage(user_accepted,'Send a valid link')
                else:
                    bot.sendMessage(user_accepted, 'Send a link please')
            elif g.data_status['progess_status'] == '/img_site':
                if content_type == 'photo':
                    g.data_status['file_code'] = msg['photo'][-1]['file_id']
                    send_request('addsite',g.data_status)
                else:
                    bot.sendMessage(user_accepted, 'Send a photo please')

            else:
                reset_status()
        elif g.chat_status == 'job':
            bot.sendMessage(user_accepted,'I\'m working for you now ❤\nPlease Wait! 🙃')
        else:
            reset_status()

def register_site(dom):
    bot.sendMessage(user_accepted, f'Starting to register the domain: {dom} 🌍\nThis can take about 2 minute of job 😴')
    status = domain_reg.register_domain(dom)
    if status == 'ok':
        bot.sendMessage(user_accepted,f'All Done! 🌍,\nThe domain ({dom}) is registred!')
        return True
    else:
        bot.sendMessage(user_accepted,f'Oh no! ❌\nThe registration failed!\nStatus: {status}')
        return False

def clear_dns_name(res):
    if '//' in res:
        res = res.split('//')[-1]
        if '/' in res:
            res = res.split('/')[0]

    return cf_getZone(res.replace('/','').replace(':',''))


def getHostingConf(dom,path):
    conf =   'server {\n'
    conf +=  '    listen 80;\n'
    conf += f'    server_name {dom};\n'
    conf +=  '    location / {\n'
    conf += f'        root {path};\n'
    conf +=  '        try_files $uri $uri/ / = 404;\n'
    conf +=  '        index index.htm index.html;\n'
    conf +=  '    }\n}\n'
    return conf

def getRedirectConf(dom,path):
    conf =  'server {\n'
    conf +=  '    listen 80;\n'
    conf += f'    server_name {dom};\n'
    conf += f'    return 302 {path};\n'
    conf +=  '}\n'
    return conf


def setUpNginxHost(dom):
    bot.sendMessage(user_accepted,'Configuring Nginx Hosting! 🌍')
    try:
        path_host = os.path.join(SITE_DIR,dom.replace('.','_'))
        if not os.path.exists(path_host):
            os.mkdir(path_host)
            set_permission(path_host)
            with open(CLOUDFLARE_CONFG_FILE,'at') as config_ng:
                config_ng.write('\n\n')
                config_ng.write(getHostingConf(dom,path_host))
            try:
                bot.sendMessage(user_accepted, f'Restarting Nginx...')
                subprocess.run(["/bin/systemctl", "restart", 'nginx'])
                bot.sendMessage(user_accepted, f'All Done ✅')
                return path_host
            except Exception as e:
                bot.sendMessage(user_accepted, f'Command failed! ❌\nResponse: {e}')
    except:pass
    bot.sendMessage(user_accepted, 'Configuring Nginx Failed!❌\nNeed Human Interaction 🙃')
    return False

def setUpNginxRedirect(dom,redirect_to):
    bot.sendMessage(user_accepted,'Configuring Nginx Redirect! 🌍')
    try:
        with open(CLOUDFLARE_CONFG_FILE,'at') as config_ng:
            config_ng.write('\n\n')
            config_ng.write(getRedirectConf(dom,redirect_to))
        try:
            bot.sendMessage(user_accepted, f'Restarting Nginx...')
            subprocess.run(["/bin/systemctl", "restart", 'nginx'])
            bot.sendMessage(user_accepted, f'All Done ✅')
            return True
        except Exception as e:
            bot.sendMessage(user_accepted, f'Command failed! ❌\nResponse: {e}')
    except:pass
    bot.sendMessage(user_accepted, 'Configuring Nginx Failed!❌\nNeed Human Interaction 🙃')
    return False

def take_on_cloudflare(dom):
    bot.sendMessage(user_accepted, f'Starting to pass {dom} to Cloudflare 🌍\nThis can take about 1:30 minutes of job 😴')
    try:
        adm_pane = CloudAdmin()
        try:
            adm_pane.subscribe_zone(dom)
        except Exception as e:
            if "is not a registered domain" in str(e):
                bot.sendMessage(user_accepted,'Cloudflare subscription failed...\nWait 2 minutes and i\'ll try again')
                sleep(60*2)
                adm_pane.subscribe_zone(dom)
            elif "already exists" in str(e):
                bot.sendMessage(user_accepted,"Cloudflare registration skipped... Already Registred!")
                return True
            else:
                raise e

        sleep(1)
        bot.sendMessage(user_accepted,'Domain subscribed to Cloudflare! 🌍\nSetting up some settings...')
        status = cloudflare_setup.set_up_domain(dom)
        if status == 'ok':
            bot.sendMessage(user_accepted, f'All Done! ✅\n')
        else:
            bot.sendMessage(user_accepted,f'Settings not setted correctly! ❌\nNeed human interaction, Status: {status}\nContinuing...')
        return True
    except Exception as e:
        bot.sendMessage(user_accepted,f'Passing to Cloudflare Failed! ❌\nStatus: {e}')
        return False


def containOnly(word, letters):
    for ele in word:
        if ele not in letters:
            return False
    return True

def get_yt_html_page(yt_code):
    return """
    <html><head>
    <title>Site Web</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <script>
    if (location.protocol !== 'https:') {
            location.replace(`https:${location.href.substring(location.protocol.length)}`);
    }
    </script>
    </head>
    <body><style>*{margin:0;}iframe{width: 100vw;height: 100vh;}</style>
    <iframe src="https://www.youtube.com/embed/"""+yt_code+"""?autoplay=1" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
    </body></html>
    """

def get_img_html_page():
   return """
   <html>
        <head>
                <title>Site Web</title>
                <link rel="icon" href="/evilneko.jpg">
                <meta charset="utf-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
        <script>
        if (location.protocol !== 'https:') {
                    location.replace(`https:${location.href.substring(location.protocol.length)}`);
        }
         </script>
        </head>
        <style>
                html, body{
                        margin:0;
                        width:100%;
                        background-color:#000;
                        color:#FFF;
                }

                * {
                        max-width:100vw;
                        max-height:100vh;
                }
                img {
                        max-width: 100%;
                        height: auto;
                        display: block;
                        margin-left: auto;
                        margin-right: auto;
                        margin: 0;
                        position: absolute;
                        top: 50%;
                        left: 50%;
                        -ms-transform: translate(-50%, -50%);
                        transform: translate(-50%, -50%);

                }
        </style>
        <body>
                <img src="/evilneko.jpg"></img>
        </body>

</html>
   """

def set_up_site(dom,lastOp,paramethers=()):

    if not register_site(dom):
        bot.sendMessage(user_accepted,'Trying to continue...')
    else:
        bot.sendMessage(user_accepted, 'Waiting 3 minutes for completing global registration 🌍...')
        sleep(3*60)
    if take_on_cloudflare(dom):
        return lastOp(dom,*paramethers)
    return False

def html_site(site_html):
    opath = set_up_site(g.data_status['dns_to_register'],setUpNginxHost)
    if opath != False:
        bot.sendMessage(user_accepted, 'Configuring html page 📄')
        path = os.path.join(opath,'index.html')
        try:
            with open(path,'wt') as f_index:
                f_index.write(site_html)
                set_permission(path)
                bot.sendMessage(user_accepted, f'Index Creation Completed! ✅')
                return opath
        except:
            bot.sendMessage(user_accepted, f'Index Creation Failed! ❌')
    else:
        bot.sendMessage(user_accepted, f'Site Creation Failed! ❌')
    return False

bot = telepot.Bot('REDACTED')
bot.sendMessage(user_accepted,'Now I\'m On! ✅🌍')
MessageLoop(bot, message_manager).run_as_thread()

while True:
    if g.chat_status == 'job':
        if g.data_status['request'] == 'public_ip':
            public_ip, _ = my_ip_address()
            bot.sendMessage(user_accepted, f'This is my public IP 🌍: {public_ip}')
        elif g.data_status['request'] == 'ddns_hit':
            try:
                dns = get_cf_config()['target_dns']
                CloudAdmin().ddns(dns)
                bot.sendMessage(user_accepted, f'DDNS Operation Success on {dns}')
            except Exception as e:
                bot.sendMessage(user_accepted, f'DDNS Failed, Error:\n{e}')
        elif g.data_status['request'] == 'service_restart':
            if g.data_status['name'] == False:
                try:
                    res = subprocess.run(["/bin/sh", "restart_command.sh"], capture_output=True).stdout.decode()
                    bot.sendMessage(user_accepted,f'Command executed! ✅\nResponse: {res}')
                except Exception as e:
                    bot.sendMessage(user_accepted, f'Command failed! ❌\nResponse: {e}')

            else:
                srv_name = g.data_status['name']
                if containOnly(srv_name,'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-'):
                    try:
                        res = subprocess.run(["/bin/systemctl", "restart", srv_name], capture_output=True).stdout.decode()
                        bot.sendMessage(user_accepted, f'Command executed! ✅\nResponse: {res}')
                    except Exception as e:
                        bot.sendMessage(user_accepted, f'Command failed! ❌\nResponse: {e}')
                else:
                    bot.sendMessage(user_accepted,'Security control Failed ⚠️⛔\nUse only letters, number and _,-')
        elif g.data_status['request'] == 'addsite':
            if g.data_status['progess_status'] == '/blank_site':
                html_site("<html><script>if (location.protocol !== 'https:') {location.replace(`https:${location.href.substring(location.protocol.length)}`);}</script></html>")
            elif g.data_status['progess_status'] == '/yt_video_site':
                html_site(get_yt_html_page(g.data_status['yt_video_code']))
            elif g.data_status['progess_status'] == '/redirect_site':
                set_up_site(g.data_status['dns_to_register'],setUpNginxRedirect,(g.data_status['redirect_link'],))
            elif g.data_status['progess_status'] == '/img_site':
                res = html_site(get_img_html_page()) 
                if res != False:
                    path = os.path.join(res,'evilneko.jpg')
                    bot.download_file(g.data_status['file_code'], path)
                    set_permission(path)


        elif g.data_status['request'] == 'restart':
            try:
                subprocess.run(["reboot"])
            except Exception as e:
                bot.sendMessage(user_accepted, f'Command failed! ❌\nRestart Failed!')

        reset_status()






