import { redirect } from "next/navigation";

// The admin app is mounted under /admin/*; send the index straight to the dashboard.
export default function AdminIndexPage() {
  redirect("/admin/dashboard");
}
