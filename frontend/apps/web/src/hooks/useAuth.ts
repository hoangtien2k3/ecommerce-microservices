import { useMutation } from "@tanstack/react-query";
import { authApi } from "@ecommerce/lib/api";
import { useAuthStore } from "@ecommerce/lib/store";
import { useRouter } from "@/i18n/navigation";

export function useLogin() {
  const { setAuth } = useAuthStore();
  const router = useRouter();

  return useMutation({
    mutationFn: ({ username, password }: { username: string; password: string }) =>
      authApi.login(username, password),
    onSuccess: async (res) => {
      const tokenData = res.data?.data;
      if (!tokenData?.access_token) return;

      localStorage.setItem("access_token", tokenData.access_token);
      try {
        const profileRes = await authApi.getProfile();
        const user = profileRes.data?.data;
        if (user) {
          setAuth(user, tokenData.access_token, tokenData.refresh_token);
        }
      } catch {
        setAuth(
          { id: 0, fullName: "", username: "", email: "", gender: "", roles: [] },
          tokenData.access_token,
          tokenData.refresh_token
        );
      }
      router.push("/");
      router.refresh();
    },
  });
}

export function useRegister() {
  return useMutation({
    mutationFn: (data: {
      fullName: string;
      username: string;
      email: string;
      password: string;
      gender: string;
      phone?: string;
    }) => authApi.register(data),
  });
}

export function useLogout() {
  const { logout } = useAuthStore();
  const router = useRouter();

  return useMutation({
    mutationFn: async () => {
      const refreshToken = localStorage.getItem("refresh_token");
      if (refreshToken) {
        await authApi.logout(refreshToken);
      }
    },
    onSuccess: () => {
      logout();
      router.push("/");
    },
    onError: () => {
      logout();
      router.push("/");
    },
  });
}
