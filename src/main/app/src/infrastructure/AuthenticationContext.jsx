import * as React from "react";

export const authentication = {
  username: 'Gast',
  jwt: ''
}

export const AuthenticationContext = React.createContext(
    authentication
);