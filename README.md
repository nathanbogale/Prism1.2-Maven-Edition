# Prism1.2-Maven-Edition
Taking Prism SDK V1.2 for a run.
Previously tried here, https://github.com/nathanbogale/Prism1.2TestRun.git (a grade kotlin project following the prisim documentation),but failing as the contineous problem of loading the required dependencies happened, Project moved here for a test through maven build. 

### Below i have listed resolutions made to solve the problems faced and showstoppers to show what is currently holding me down.
##### Here is my JDK, Gradle, Kotlin& Maven Version
![image](https://user-images.githubusercontent.com/13464651/139879930-39dcc2e6-5f12-4708-b8ac-56b3ebfbd015.png)


# Resolutions


#### Changed the project to support Maven 
- And added the dependecies to be fetched to my project POM file like below

![image](https://user-images.githubusercontent.com/13464651/139877691-421da7a7-f032-44d3-a1e3-68e11932760a.png)



- Since that is not enough, as is can not find the files (Jar & POM of the dependencies), I have downloaded from Github prism repo and added them in my local maven repo 
(Maven repo folder will be created at runtime):
![image](https://user-images.githubusercontent.com/13464651/139877941-30789607-41ae-442c-a3ec-ef431ae77940.png)



- And now the import working Fine (Casue dependency issue has been resolved only for the main 5 libraries)
![image](https://user-images.githubusercontent.com/13464651/139874725-8b26d901-438b-4b25-986c-7d3cb9e300e7.png)




# Showstoppers
- ~~Ptotos and pbandk plugin not found with the specified path (iohk repo of maven or github)~~
   - ~~I do not have the files them selvs to add localy, in order to add them (anybody that do please share)~~
   - ~~They are available on Maven public(the Jar and related files) of versions up to 0.20.5, but i believe these are not the correct version Prism is looking for~~
- ~~Code not recognizing DID function~~  

- ~~This is the most recent showstopper (which is highlighting a problem with the elliptic curve keypair generation line)~~



