import React from "react";
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'
import Import from "./Import";
import ImportJobDetail from "./domain/ImportJobDetail"
import NotFound from "./infrastructure/NotFound";
import {
  ApplicationConfigurationContext,
  configuration
} from "./infrastructure/ApplicationConfigurationContext";

export function urlRouter() {
  return (

      <ApplicationConfigurationContext.Provider value={configuration}>
        <Router>
          <Switch>
            <Route path="/app" exact component={Import}></Route>
            <Route path="/app/import/job/:id"
                   component={ImportJobDetail}></Route>
            <Route path="*" exact component={NotFound}></Route>
          </Switch>
        </Router>
      </ApplicationConfigurationContext.Provider>
  );
}
