import React, { useState, useEffect } from "react";
import "../styles/ArticleBrowser.css";

const fetchData = async (limit: number, setItems: Function) => {
  try {
    const response = await fetch(
      `http://localhost:3000/article/latest?limit=${Math.max(1, limit)}`
    );
    if (response.ok) {
      const data = await response.json();
      console.log("Data from server:", data);
      setItems(data);
    } else {
      console.error("Error fetching data");
    }
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

function ArticleList() {
  const [items, setItems] = useState<
    { id: number; title: string; link: string }[]
  >([]);
  const [limit, setLimit] = useState<number>(10);
  const [isLinkVisible, setIsLinkVisible] = useState<boolean>(false);
  const [isPageLoaded, setIsPageLoaded] = useState<boolean>(false);

  useEffect(() => {
    if (!isPageLoaded) {
      fetchData(limit, setItems);
      setIsPageLoaded(true);
    }
  }, [isPageLoaded, limit]);

  const handleItemClick = (id: number, link: string) => {
    console.log(`Item clicked with id: ${id} and link:${link}`);
    window.open(link);
  };

  const handleLimitChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newLimit = parseInt(e.target.value, 10);
    setLimit(Math.max(1, newLimit));
  };

  const handleToggleLinkVisibility = () => {
    setIsLinkVisible(!isLinkVisible);
  };

  const handleFormSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    await fetchData(limit, setItems);
  };

  return (
    <div className="ArticleBrowser">
      <h1>Article browser</h1>
      <div className="ButtonPanel">
        <form onSubmit={handleFormSubmit}>
          <label>
            Limit:
            <input type="number" value={limit} onChange={handleLimitChange} />
          </label>
          <button type="submit">Apply</button>
        </form>
        <button onClick={handleToggleLinkVisibility}>
          {isLinkVisible ? "Hide Link" : "Show Link"}
        </button>
      </div>

      {limit > 0 && items.length === 0 && <h2>Items not found</h2>}
      {limit > 0 && items.length > 0 && (
        <table className="ArticleTable">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              {isLinkVisible && <th>Link</th>}
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.link}>
                <td>{item.id}</td>
                <td>{item.title}</td>
                {isLinkVisible && <td>{item.link}</td>}
                <td>
                  <button onClick={() => handleItemClick(item.id, item.link)}>
                    Open Article
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default ArticleList;
