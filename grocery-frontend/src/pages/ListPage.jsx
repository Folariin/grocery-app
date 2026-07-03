import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "../styles/list.css";

export default function ListPage() {
  const nav = useNavigate();
  const { listId } = useParams();

  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");

  // add item form
  const [itemName, setItemName] = useState("");
  const [quantity, setQuantity] = useState("1");
  const [unit, setUnit] = useState("");
  const [notes, setNotes] = useState("");

  const stats = useMemo(() => {
    const total = items.length;
    const purchased = items.filter((i) => i.status === "PURCHASED").length;
    return { total, purchased };
  }, [items]);

  useEffect(() => {
    let ignore = false;

    async function fetchItems() {
      setErr("");
      try {
        const res = await api.get(`/api/lists/${listId}/items`);
        if (!ignore) setItems(res.data);
      } catch {
        if (!ignore) setErr("Could not load items.");
      }
    }

    fetchItems();
    return () => {
      ignore = true;
    };
  }, [listId]);

  async function addItem(e) {
    e.preventDefault();
    const name = itemName.trim();
    if (!name) return;

    setErr("");
    try {
      const q = quantity.trim() ? Number(quantity) : 1;

      const res = await api.post(`/api/lists/${listId}/items`, {
        itemName: name,
        quantity: Number.isFinite(q) && q > 0 ? q : 1,
        unit: unit.trim() ? unit.trim() : null,
        notes: notes.trim() ? notes.trim() : null,
      });

      setItems((prev) => [res.data, ...prev]);
      setItemName("");
      setQuantity("1");
      setUnit("");
      setNotes("");
    } catch {
      setErr("Could not add item.");
    }
  }

  async function togglePurchased(listItemId, currentlyPurchased) {
    setErr("");

    // optimistic UI update
    setItems((prev) =>
      prev.map((it) =>
        it.id === listItemId
          ? {
              ...it,
              status: currentlyPurchased ? "NEEDED" : "PURCHASED",
            }
          : it
      )
    );

    try {
      await api.patch(`/api/list-items/${listItemId}/purchase`, {
        purchased: !currentlyPurchased,
      });
    } catch {
      // revert on failure
      setItems((prev) =>
        prev.map((it) =>
          it.id === listItemId
            ? { ...it, status: currentlyPurchased ? "PURCHASED" : "NEEDED" }
            : it
        )
      );
      setErr("Could not update purchase status.");
    }
  }

  async function removeItem(listItemId) {
    setErr("");

    // optimistic remove
    const snapshot = items;
    setItems((prev) => prev.filter((it) => it.id !== listItemId));

    try {
      await api.delete(`/api/list-items/${listItemId}`);
    } catch {
      setItems(snapshot);
      setErr("Could not remove item.");
    }
  }

  return (
    <div className="page list-page">
      <div className="container">
        <header className="topbar">
          <div className="titleBlock">
            <button className="linkBtn" onClick={() => nav(-1)}>
              ← Back
            </button>
            <div>
              <h2>Grocery List</h2>
              <div className="statPill">
                {stats.purchased}/{stats.total} purchased
              </div>
            </div>
          </div>
        </header>

        <form className="addCard" onSubmit={addItem}>
          <div className="row">
            <input
              className="nameInput"
              placeholder="Item name (e.g., Milk)"
              value={itemName}
              onChange={(e) => setItemName(e.target.value)}
            />
            <input
              className="qtyInput"
              placeholder="Qty"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
              inputMode="numeric"
            />
            <input
              className="unitInput"
              placeholder="Unit"
              value={unit}
              onChange={(e) => setUnit(e.target.value)}
            />
            <button className="primaryBtn" type="submit">
              Add
            </button>
          </div>

          <div className="row">
            <input
              className="notesInput"
              placeholder="Notes (optional)"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
            />
          </div>
        </form>

        {err && <div className="error">{err}</div>}

        <div className="list">
          {items.map((it) => {
            const purchased = it.status === "PURCHASED";
            return (
              <div key={it.id} className={`itemRow ${purchased ? "done" : ""}`}>
                <button
                  type="button"
                  className={`check ${purchased ? "checked" : ""}`}
                  onClick={() => togglePurchased(it.id, purchased)}
                  aria-label="toggle purchased"
                >
                  {purchased ? "✓" : ""}
                </button>

                <div className="itemMain">
                  <div className="itemTop">
                    <div className="itemName">{it.itemName}</div>
                    <div className="itemMeta">
                      {it.quantity ? it.quantity : 1}
                      {it.unit ? ` ${it.unit}` : ""}
                    </div>
                  </div>

                  {it.notes && <div className="itemNotes">{it.notes}</div>}
                </div>

                <button
                  type="button"
                  className="trash"
                  onClick={() => removeItem(it.id)}
                  aria-label="remove item"
                >
                  🗑
                </button>
              </div>
            );
          })}

          {items.length === 0 && (
            <div className="empty">No items yet. Add your first one above.</div>
          )}
        </div>
      </div>
    </div>
  );
}
