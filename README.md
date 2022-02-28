# DWS
Desktop Web Scheduler allows you to configure URLs that will be called in definable intervals. This can be used as a desktop based web cron scheduler (such as https://www.easycron.com/ etc.).
I created DWS in order to have my Nextcloud run its background jobs peridoically, without having a webcron service or a vserver where a cronjob can be set up.


DWS will start minimized in your OS taskbar. The color of the icon will change according to the status of the jobs running:

![taskbar](https://user-images.githubusercontent.com/7944573/155946460-1b60971a-6330-4cec-9e4e-a635bf72e598.png)

You can add an URL as well as an interval:

![2](https://user-images.githubusercontent.com/7944573/155946908-b6b93f27-bfee-4074-877f-1560f9569a3b.png)


The main window will show the configured URLs and their last state indicated by a checkmark, questionmark or an X:

![3](https://user-images.githubusercontent.com/7944573/155946995-d92e884d-4d6f-455f-a32a-50198c173983.png)


If a scheduled call fails (as in returns a non 200-399 status code), you will be informed accordingly:

![4](https://user-images.githubusercontent.com/7944573/155947081-195930d8-710b-440f-b454-a45b2198e93c.png)


Double clicking a job will display info about the last request sent:

![6](https://user-images.githubusercontent.com/7944573/155947208-b53926c2-c3c2-4961-a297-3d6dff5889f7.png)
