import { useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AppContext } from "../../context/AppContext";
import { latestOrders } from "../../Service/OrderService";
import { verifyPayment } from "../../Service/PaymentService";
const Success = () => {
  const navigate = useNavigate();
  const { setOrderDetails, clearCart } = useContext(AppContext);

  useEffect(() => {
  async function handleStripeFlow() {
    const sessionId = new URLSearchParams(window.location.search).get("session_id");
    const orderId = localStorage.getItem("latestOrderId");
    // STEP 1 — 先校验 Stripe 支付
    if (sessionId && orderId) {
      try {
        await verifyPayment({ orderId, sessionId });
        console.log("Stripe Payment Verified ✔");
      } catch (err) {
        console.error("Stripe verification failed:", err);
      }
    }

    // STEP 2 — 最新订单只有一个（最新创建时间）
    if (localStorage.getItem("stripePaid") === "true") {
      try {
        const res = await latestOrders();
        const latestOrder = res.data[0]; // 取最新的那条订单

        setOrderDetails(latestOrder);

        // cleanup
        localStorage.removeItem("stripePaid");
        clearCart();
      } catch (err) {
        console.error("Failed to load latest order:", err);
      }
    }
  }
  handleStripeFlow();
}, []);


  return (
    <div style={{ padding: "50px", textAlign: "center", color: "white" }}>
      <h1>Payment Successful 🎉</h1>
      <p>Your payment was processed successfully.</p>

      <button
        onClick={() => navigate("/explore")}
        style={{
          marginTop: "20px",
          padding: "10px 20px",
          backgroundColor: "#4CAF50",
          color: "white"
        }}>
        Back to Explore
      </button>
    </div>
  );
};

export default Success;
