import React from "react";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Button from "@material-ui/core/Button";
import IconButton from "@material-ui/core/IconButton";

export default function Navigation(props) {

  return (<AppBar>
    <Toolbar>
      <Button variant="outlined" onClick={() => {
        props.onLocaleChange('DE-de')
      }}>DE</Button>
      <Button variant="outlined" onClick={() => {
        props.onLocaleChange('EN-en')
      }}>EN</Button>
      <IconButton edge="end" color="inherit">
      </IconButton>
    </Toolbar>
  </AppBar>)
}
