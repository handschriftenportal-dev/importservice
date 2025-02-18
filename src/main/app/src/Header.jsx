import React from "react";
import {Grid, Hidden, Typography} from "@material-ui/core";
import {ImportHeader} from "./css/ImportHeader";

function Header(props) {
  return <ImportHeader component="header">
    <Grid container justify="center" alignItems="center"
          direction="column">
      <Hidden>
        <Grid item md={1}></Grid>
      </Hidden>
      <Grid item xs={12} md={10}>
        <Typography id="uploadheadline" variant="h3"
                    component="h1">{props.title}</Typography>
      </Grid>
      <Hidden>
        <Grid item md={1}></Grid>
      </Hidden>
    </Grid>
  </ImportHeader>
}

export default Header;
