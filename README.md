# localmarket



1. Web Server에 apklist.json, apk파일, icon을 올린다.

apklist.json 내용
```swift
{
 "itemList" :
 [
   {
    "applicationName" : "localmarket",
    "applicationVersion" : "1.001",
    "applicationIcon" : "favicon.ico"
   },
   {
    "applicationName" : "calendar",
    "applicationVersion" : "1.033",
    "applicationIcon" : "calendar.png"
   },
   {
    "applicationName" : "localchat",
    "applicationVersion" : "1.00",
    "applicationIcon" : "loading.png"
   },
   {
    "applicationName" : "calculator",
    "applicationVersion" : "1.05",
    "applicationIcon" : "calculator.png"
   }
 ]
}
```

2. Web Server로부터 itemlist 파일을 읽어와 Application 이름 , 버전, Icon을 받아와서 화면에 뿌린다.

<img width="300" src="https://user-images.githubusercontent.com/12454018/148693012-2e85e67b-7600-48e1-ac68-064b8b5ea48c.png">

3. Download 버튼을 클릭하여 apk파일을 다운로드 받은 후 Install을 진행한다 .

<img width="300" src="https://user-images.githubusercontent.com/12454018/148693019-1f215f7f-1b40-4256-81f7-4851ae04608e.png">

