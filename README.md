# Telegram bot for marketplace megamarket.ru

This bot will help you find the most beneficial purchase options.

1. Send a link to the product you are interested in.
2. Specify your SberSpasibo level in the settings.
3. Provide any available promo codes.
4. Set up notification filters according to your preferences.  

The bot will send a notification if an offer matching your parameters becomes available.


## How to use (Ubuntu)
### 1. Install Docker:
Make sure you have Docker installed on your computer. 
Follow the [instructions](https://docs.docker.com/engine/install/ubuntu/).
### 2. Install Google Chrome:
```sh
#!/bin/bash

wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
sudo dpkg -i google-chrome-stable_current_amd64.deb
sudo apt --fix-broken install -y
```
### 3. Clone this repository and make `mvnw` executable:
```sh
#!/bin/bash

git clone https://github.com/schegolevalex/mm-parser.git
cd ./mm-parser/
chmod +x ./mvnw
```
### 4. Specify Your Own Authenticated Proxy List (Optional):

Create a file named `proxy_list` and place it in the `mm-parser/src/main/resources` folder. 
The file should contain a list of proxies in the following format:
```
{username1}:{password1}@{host1}:{port1}
{username2}:{password2}@{host2}:{port2}
...
```
### 5. Create Your Own Telegram Bot:
Use [@BotFather](https://t.me/BotFather) on Telegram to create your bot. 
Save the bot name and bot token parameters.

### 6. Set environment variables 
```sh
#!/bin/bash

export BOT_USERNAME={YOUR_BOT_NAME}
export BOT_TOKEN={YOUR_BOT_TOKEN}
export CREATOR_ID={YOUR_BOT_NAME}
```
You can see your `CREATOR_ID` using [@userinfobot](https://t.me/userinfobot).

### 7. Execute the command to run the Bot:
```sh
#!/bin/bash

./mvnw spring-boot:run
```