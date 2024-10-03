# Keycloak Attributes Verification Provider

***[Warning: this provider is currently a work in progress]***

## Introduction

This provider allows user attribute to be verified via OTP method where generated challenge values (e.g. 6 digit numbers) can be sent to the user via SMS or email in order to verify corresponding user attribute values are genuine.

## High Level Design

1. Upon a user entering/updating an attribute, if the attribute requires a verification then the verification process for the attribute will begin.
2. The user will be presented with a form that can be used to trigger a mechanism that generates, stores and sends a six-digit challenge value to the user via SMS, email etc.
3. The user can then obtain the challenge value and enter it in an input field in the form and submit it to be verified.
4. If the challenge value that is entered in the form matches the value stored in the database, the attribute is flagged verified. Otherwise, the user will be presented with the same form.
5. The user will also have the option to remove the attribute value in order to escape from the verification process.