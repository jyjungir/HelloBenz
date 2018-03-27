# HelloBenz
This is a small android watch App prototype which allow users to unlock Mercedes-Benz Car doors by clicking a button.

This app uses Mercedes-Benz's connected car API to communicate with the car.
In order to access the API services, we need to authenticate ourselves by their OAuth API.
Note that this small prototype skips the initial authentication steps, so the access token is currently fixed in code and need to modify manually. 

Authentication process steps document:
https://developer.mercedes-benz.com/content-page/oauth-documentation

Connected-Car API document:
https://developer.mercedes-benz.com/apis/connected_vehicle_experimental_api/docs

