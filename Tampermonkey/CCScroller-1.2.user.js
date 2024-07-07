// ==UserScript==
// @name         CCScroller
// @namespace    https://domy.sh/
// @version      1.2
// @description  CC Scoreboard reloader and scroller
// @author       DomySh
// @match        https://ctf.cyberchallenge.it/*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=cyberchallenge.it
// @grant        none
// ==/UserScript==

(function() {
    'use strict';

    const styles = `
     .textsuffix{
        position: absolute;
        right: 25px;
        top: 6px;
     }
     .textsuffixcontainer{
        display: inline-block;
        position: relative;
     }
     .textsuffixcontainer > input::-webkit-outer-spin-button,
     .textsuffixcontainer > input::-webkit-inner-spin-button {
        -webkit-appearance: none;
        margin: 0;
     }

     /* Firefox */
     .textsuffixcontainer > input[type=number] {
        -moz-appearance: textfield;
     }
    `



    const featureHTML = '<div class="py-3 bg-body" id="refresh-commands"><h4 style="text-align:center; padding-bottom:10px">Fullscreen refresh and scroll</h4><div class="container"><div class="justify-content-center align-items-center row"><div class="col-lg-5 col-12 textsuffixcontainer"><input placeholder="Refresh timeout" min="1" type="number" class="form-control" id="refresh-seconds"><span class="textsuffix">sec.</span></div><div class="mt-3 mt-lg-0 col-lg-3 col-12"><div class="form-check"><input type="checkbox" class="form-check-input" id="enable-refresh"><label for="enable-refresh" class="form-check-label">Enable Refresh</label></div></div></div></div></div>';
    const stopButtonHTML = '<button style="position:fixed; top:30px; right:30px; z-index:1000; font-size:20px; background-color:#d90429; width:30px; height:30px; display:flex; align-items:center; justify-content:center; border-radius:30px; border: none; font-weight: bolder;" id="exit-refresh">X</button>';

    let intervalrules = null

    function setuppage(){
        console.log("RELOADING")
        location.reload()
        console.log("SEARCH TABLE")
    }
    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }
    function scrollBottonInTime(time, starttime=0, lasttime=0){
        if (starttime == 0) lasttime = starttime = new Date().getTime()
        const thistime = new Date().getTime()
        const timelapsed = thistime - starttime
        const space = document.body.scrollHeight-document.body.offsetHeight
        const position = window.scrollY
        const timetobottom = time-timelapsed
        const spacetogo = space-position
        const velocity = spacetogo/timetobottom
        const offsettoadd = Math.round(((thistime-lasttime)*(velocity)))
        if (spacetogo > 0 && timetobottom > 0){
            if (offsettoadd>0){
                window.scrollTo({ top:offsettoadd+position, left:0, behavior: "instant"})
                setTimeout(()=>scrollBottonInTime(time, starttime, thistime), 5)
            }else{
                setTimeout(()=>scrollBottonInTime(time, starttime, lasttime), 5)
            }
        }
    }
    async function start_refreshing(time){
        console.log("TABLE SETING")
        document.body.insertBefore(document.getElementsByClassName("table-responsive")[0], document.body.children[0])
        document.body.removeChild(document.body.children[1])
        document.body.removeChild(document.body.children[1])
        document.body.appendChild(htmlParser(stopButtonHTML))
        const stopBtn = document.querySelector("#exit-refresh")
        stopBtn.addEventListener("click", ()=>{
            localStorage.setItem("CCScrollEnabled", "0")
            setuppage()
        });
        console.log("WAIT FOR NEW REFRESH")
        setTimeout(setuppage, time)
        await sleep(500)
        scrollBottonInTime(time-500)
    }

    function htmlParser(text){
        return new DOMParser().parseFromString(text, "text/html").body.childNodes[0];
    }

    function getEnabledValue(){
        return parseInt(localStorage.getItem("CCScrollEnabled")??0) != 0
    }

    function getSecondsValue(){
        return parseInt(localStorage.getItem("CCScrollRefreshSeconds")??30)
    }
    function cleanUp(){
        document.querySelector("#refresh-commands")?.remove()
        document.querySelector("#exit-refresh")?.remove()
        if (intervalrules != null) clearInterval(intervalrules)
        intervalrules = null
    }

    async function main(){
        while(document.getElementsByClassName("table-responsive").length != 1 && document.querySelectorAll("#root > div > div:nth-child(3) > div > div > div:nth-child(2)").lenght != 1){
            console.log("NOT FOUND: SLEEP")
            await sleep(100)
        }
        const enabled = getEnabledValue()
        const seconds = getSecondsValue()
        document.querySelectorAll('#root > div')[1].insertBefore(htmlParser(featureHTML), document.querySelectorAll('#root > div')[1].childNodes[4])
        const enableRefresh = document.querySelector("#enable-refresh")
        enableRefresh.checked = enabled
        enableRefresh.addEventListener("click", ()=>{
            localStorage.setItem("CCScrollEnabled", enableRefresh.checked?"1":"0")
            if (enableRefresh.checked){
                start_refreshing(getSecondsValue()*1000)
            }
        });
        const refreshTime = document.querySelector("#refresh-seconds")
        refreshTime.value = seconds
        refreshTime.addEventListener("change", ()=>{
            try{
                const value = parseInt(refreshTime.value)
                if (value > 0) localStorage.setItem("CCScrollRefreshSeconds", value.toString())
            }catch(e){}
        })
        intervalrules = setInterval(()=>{
            document.querySelector("#root > div > div:nth-child(3) > div > div > div:nth-child(2)").className = "mt-3 mt-lg-0 col-lg-3 col-12"
        }, 100) //Minor graphic fix
        if (enabled){
            start_refreshing(seconds*1000)
        }
    }

    function conditionalRunner(){
        cleanUp()
        if (location.pathname == "/scoreboard"){
            console.log("INIT")
            main()
        }
    }

    //Add path change features

    let oldPushState = history.pushState;
    history.pushState = function pushState() {
        let ret = oldPushState.apply(this, arguments);
        window.dispatchEvent(new Event('pushstate'));
        window.dispatchEvent(new Event('locationchange'));
        return ret;
    };

    let oldReplaceState = history.replaceState;
    history.replaceState = function replaceState() {
        let ret = oldReplaceState.apply(this, arguments);
        window.dispatchEvent(new Event('replacestate'));
        window.dispatchEvent(new Event('locationchange'));
        return ret;
    };

    //Trigger script every time

    window.addEventListener("locationchange", conditionalRunner);
    window.addEventListener("DOMContentLoaded", conditionalRunner);
    conditionalRunner();
    const styleSheet = document.createElement("style")
    styleSheet.innerText = styles
    document.head.appendChild(styleSheet)

})();