import React from "react";
import {Typography} from "@material-ui/core";

export default function ErrorMessage(props) {
  return (
      <React.Fragment>
        <Typography variant="h6"
                    component="h6">Es ist ein Fehler aufgetreten</Typography>
        <Typography variant="div"
                    component="div">{props.message}</Typography>
      </React.Fragment>
  )
}
