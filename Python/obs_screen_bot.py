
#!/usr/bin/env python3

from twitchAPI.twitch import Twitch
from twitchAPI.oauth import UserAuthenticator
from twitchAPI.type import AuthScope, ChatEvent
from twitchAPI.chat import Chat, EventData, ChatMessage, ChatCommand
import asyncio
from obswebsocket import obsws, requests  # noqa: E402

OBS_PASSWORD = "REDACTED"
APP_ID = 'REDACTED'
APP_SECRET = 'REDACTED'
TARGET_CHANNEL = 'REDACTED'
OBS_HOST = "localhost"
OBS_PORT = 4455

USER_SCOPE = [AuthScope.CHAT_READ, AuthScope.CHAT_EDIT]

def set_scene_by_hashtag(tag):
    try:
        ws = obsws(OBS_HOST, OBS_PORT, OBS_PASSWORD)
        ws.connect()
        scenes = ws.call(requests.GetSceneList())
        for s in scenes.getScenes():
            name = s['sceneName']
            name = name.split()
            for ele in name:
                if ele[0] == "#" and ele[1:] == tag and ele[1:] != "":
                    print("Switching to {}".format(s['sceneName']))
                    ws.call(requests.SetCurrentProgramScene(sceneName=s['sceneName']))
                    return True
        return "Screen not found"
    except Exception:
        return "Obs not running or password is wrong"
    finally:
        ws.disconnect()

def get_scenes_tag_list():
    try:
        ws = obsws(OBS_HOST, OBS_PORT, OBS_PASSWORD)
        ws.connect()
        scenes = ws.call(requests.GetSceneList())
        tags = []
        for s in scenes.getScenes():
            name = s['sceneName']
            name = name.split()
            for ele in name:
                if ele[0] == "#" and ele[1:] != "":
                    tags.append(ele[1:])
        return tags
    except Exception:
        return "Obs not running or password is wrong"
    finally:
        ws.disconnect()


async def on_ready(ready_event: EventData):
    print('Bot is ready for work, joining channels')
    await ready_event.chat.join_room(TARGET_CHANNEL)

async def on_message(msg: ChatMessage):
    print(f'in {msg.room.name}, {msg.user.name} said: {msg.text}')

async def screen_command(cmd: ChatCommand):
    if cmd.user.mod: 
        if len(cmd.parameter) == 0:
            await cmd.reply('Tell me what screen to set 0_0')
        else:
            response = set_scene_by_hashtag(cmd.parameter)
            if response == True:
                await cmd.reply(f'{cmd.user.name}: Switched to #{cmd.parameter}')
            else:
                await cmd.reply(f'{cmd.user.name}: {response}')
    else:
        print(f'{cmd.user.name}: Is not a mod')

async def screen_list_command(cmd: ChatCommand):
    if cmd.user.mod: 
            response = get_scenes_tag_list()
            if isinstance(response, str):
                await cmd.reply(f'{cmd.user.name}: En error has occured: {response}')
            else:
                await cmd.reply(f'{cmd.user.name}: Available screens: {", ".join(response)}')
    else:
        print(f'{cmd.user.name}: Is not a mod')

async def run():
    twitch = await Twitch(APP_ID, APP_SECRET)
    auth = UserAuthenticator(twitch, USER_SCOPE)
    token, refresh_token = await auth.authenticate()
    await twitch.set_user_authentication(token, USER_SCOPE, refresh_token)

    chat = await Chat(twitch)

    chat.register_event(ChatEvent.READY, on_ready)

    chat.register_command('screen', screen_command)
    chat.register_command('screenlist', screen_list_command)

    chat.start()

    try:
        input('press enter to stop\n')
    finally:
        chat.stop()
        await twitch.close()


asyncio.run(run())

"""
requirements:

obs-websocket-py
twitchAPI
"""