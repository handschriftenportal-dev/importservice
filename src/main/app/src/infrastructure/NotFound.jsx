import React from "react";
import {Typography} from "@material-ui/core";
import {Link} from "react-router-dom";

export default function NotFound() {
  return (
      <React.Fragment>
        <Typography variant="h2"
                    component="h2">Seite konnte nicht gefunden
          werden</Typography>
        <p>
          <Link to="/app/import">Weiter zur Startseite</Link>
        </p>
      </React.Fragment>
  )
}
