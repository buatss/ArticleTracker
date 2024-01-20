import React from "react";
import "./styles/App.css";
import Navbar from "./components/Navbar";
import ArticleList from "./components/ArticleBrowser";
import ReactLogo from "./assets/react.svg";

const App: React.FC = () => {
  return (
    <div className="App">
      <header className="App-header">
        <img src={ReactLogo} className="App-logo" alt="logo" />
        <h1 className="App-title">Article Tracker</h1>
      </header>
      <Navbar />
      <ArticleList />
    </div>
  );
};

export default App;
