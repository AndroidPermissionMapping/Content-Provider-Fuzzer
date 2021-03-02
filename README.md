# Content-Provider-Fuzzer
This fuzzer runs as an app on an android device. It connects back to `Dynamo`[1] to gets the fuzzing
input from there. After perfoming the fuzzing request it reports back to `Dynamo`[1].

[1] `Dynamo` https://github.com/abdawoud/Dynamo

## Setup
1. `git clone git@github.com:AndroidPermissionMapping/Content-Provider-Fuzzer.git --recurse-submodules`
2. `cd Content-Provider-Fuzzer`
3. `ANDROID_HOME=~/android-sdk ./gradlew app:assemble`
4. The APK is in `app/build/outputs/apk/debug/app-debug.apk`

Note: Adapt `ANDROID_HOME=<sdk_path>` to your environment.